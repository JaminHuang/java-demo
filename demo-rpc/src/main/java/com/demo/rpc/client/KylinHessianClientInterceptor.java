package com.demo.rpc.client;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;
import com.demo.ibatx.core.entity.Condition;
import com.demo.rpc.support.RpcThreadLocal;
import com.demo.sdk.exception.HttpRefusedException;
import com.demo.sdk.exception.IllegalObjectException;
import com.demo.sdk.exception.ServiceException;
import com.demo.sdk.page.Page;
import com.demo.sdk.util.ExceptionUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.caucho.HessianClientInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 修改invoke方法, 增加client端loadbalance支持, 可同时
 */
public class KylinHessianClientInterceptor extends HessianClientInterceptor {

    private static Logger logger = LoggerFactory.getLogger(KylinHessianClientInterceptor.class);

    // 集合检查阈值系数
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Map<String, Object> hessianProxyMap = new ConcurrentHashMap<>();

    private HessianProxyFactory proxyFactory;

    private ServiceUrlSelector serviceUrlSelector;

    private String exportName;

    private String applicationName;

    // 如何清除是个问题
    private Class<?> inClass; // 该代理类在哪个类中使用

    // 特殊方法
    private List<String> ignoreMethods = Arrays.asList("batchList", "listByIds");

    public KylinHessianClientInterceptor() {
        super();
        proxyFactory = new HessianProxyFactory();
        super.setProxyFactory(proxyFactory); // 保持和父类中同一个factory对象
    }

    /**
     * Set the HessianProxyFactory instance to use. If not specified, a default
     * HessianProxyFactory will be created.
     * <p>
     * Allows to use an externally configured factory instance, in particular a
     * custom HessianProxyFactory subclass.
     */
    @Override
    public void setProxyFactory(HessianProxyFactory proxyFactory) {
        this.proxyFactory = (proxyFactory != null ? proxyFactory : new HessianProxyFactory());
        super.setProxyFactory(this.proxyFactory);
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        // 重试次数
        int times = 16;
        try {
            for (int i = 1; i <= times; i++) {
                try {
                    Object obj = run(invocation);
                    proofResult(invocation, obj);
                    return obj;
                } catch (Throwable ex) {
                    boolean isRetry = false;
                    List<Throwable> causeList = new ArrayList<>();
                    causeList.add(ex);
                    Throwable e;
                    for (e = ex.getCause(); e != null; e = e.getCause()) {
                        causeList.add(e);
                    }
                    // 当远程调用出现以下2种异常的时候进行重试
                    // 远程服务器连接异常HttpHostConnectException
                    // 拒绝接收http请求HttpRefusedException
                    // 三次握手成功后，http请求后与server发出FIN,server收到http,发出RST
                    for (Throwable throwable : causeList) {
                        if (throwable instanceof HttpHostConnectException
                                || throwable instanceof HttpRefusedException
                                || throwable instanceof NoHttpResponseException) {
                            isRetry = true;
                            break;
                        }
                        // 连接被重置的情况也要重试:java.net.SocketException: Connection reset
                        if ("java.net.SocketException".equals(throwable.getClass().getName())) {
                            String message = throwable.getMessage();
                            if ("Connection reset".equals(message)) {
                                isRetry = true;
                                break;
                            }
                        }
                    }
                    if (!isRetry || i >= times) {
                        throw convertHessianAccessException(ex, RpcThreadLocal.getServiceUrl(), invocation.getMethod().getName());
                    }
                    logger.info("下游服务正在优雅重启或已经关闭,进行重试");
                }
            }
        } finally {
            RpcThreadLocal.removeAll();
        }
        return null;
    }

    private Object run(MethodInvocation invocation) throws Throwable {
        String method = invocation.getMethod().getName();
        Object hessianProxy;
        String url = null;
        try {
            url = this.getServiceUrl();
            hessianProxy = hessianProxyMap.get(url);
            logger.debug("ServiceUrlSelector.[selectUrl] = {}", url);
            if (hessianProxy == null) {
                hessianProxy = proxyFactory.create(getServiceInterface(), url, getBeanClassLoader());
                hessianProxyMap.put(url, hessianProxy);
            }
        } catch (MalformedURLException ex) {
            throw new RemoteLookupFailureException("Service URL [" + url + "] is invalid", ex);
        }
        RpcThreadLocal.setServiceUrl(url);
        if (hessianProxy == null) {
            throw new IllegalStateException("HessianProxy init fail, service url : " + url);
        }
        ClassLoader originalClassLoader = overrideThreadContextClassLoader();

        try {
            return invocation.getMethod().invoke(hessianProxy, invocation.getArguments());
        } catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            // Hessian 4.0 check: another layer of InvocationTargetException.
            if (targetEx instanceof InvocationTargetException) {
                targetEx = ((InvocationTargetException) targetEx).getTargetException();
            }

            if (targetEx instanceof UndeclaredThrowableException) {
                UndeclaredThrowableException utEx = (UndeclaredThrowableException) targetEx;
                targetEx = utEx.getUndeclaredThrowable();
            }
            logger.error(ExceptionUtils.getExceptionMsg(ex));
            logger.error("[url] = {}, [method] = {}, [inClass] = {}", url, method, inClass);
            throw targetEx;
        } finally {
            resetThreadContextClassLoader(originalClassLoader);
        }
    }


    /**
     * 检查集合长度
     */
    private void proofResult(MethodInvocation invocation, Object obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj instanceof Collection) {
                Collection c = (Collection) obj;
                int threshold = (int) (Page.getMaxRow() * DEFAULT_LOAD_FACTOR);
                // 如果返回集合大小大于阈值，视情况是否进行消息提醒
                if (c.size() >= threshold) {
                    Object[] args = invocation.getArguments();
                    if (args != null) {
                        for (Object arg : args) {
                            if (arg instanceof Page) {
                                Page page = (Page) arg;
                                Integer row = page.getRow();
                                // 当请求行数等于maxRow - 1不需要提醒
                                if (row != null && row == (Page.getMaxRow() - 1)) {
                                    return;
                                }
                            } else if (arg instanceof Condition) {
                                Condition condition = (Condition) arg;
                                if (Objects.nonNull(condition.getLimitCondition())) {
                                    int row = condition.getLimitCondition().getLimit();
                                    // 当请求行数等于maxRow - 1不需要提醒
                                    if (row == Page.getMaxRow() - 1) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    String note = "core函数返回集合长度超出阈值:" + threshold + ", 实际长度:" + c.size();
                    String url = RpcThreadLocal.getServiceUrl();
                    String method = invocation.getMethod().getName();
                    // 特殊方法不提示
                    if (ignoreMethods.contains(method) && c.size() <= Page.getMaxRow()) {
                        return;
                    }
                    JSONObject json = new JSONObject(true);
                    json.put("url", url);
                    json.put("method", method);
                    json.put("args", args);
                    json.put("note", note);
                    json.put("inClass", inClass.getName());
                    logger.info(json.toJSONString());
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getExceptionMsg(e));
        }
    }

    /**
     * Convert the given Hessian access exception to an appropriate Spring
     * RemoteAccessException.
     *
     * @param e the exception to convert
     * @return the RemoteAccessException to throw
     */
    protected RuntimeException convertHessianAccessException(Throwable e, String url, String method) {
        // 连接异常
        if (e instanceof HessianConnectionException || e instanceof ConnectException) {
            return new RemoteConnectFailureException("Cannot connect to Hessian remote service at [" + url + "], method = [" + method + "], inClass = [" + inClass.getName() + "]", e);
        }
        // 断言异常
        else if (e instanceof IllegalArgumentException || e instanceof IllegalObjectException) {
            throw new ServiceException(e.getMessage());
        }
        // service异常
        else if (e instanceof ServiceException) {
            throw new ServiceException(((ServiceException) e).getErrorCode(), e.getMessage());
        }
        // 其他异常
        else {
            return new RemoteAccessException("Cannot access Hessian remote service at [" + url + "], method = [" + method + "], inClass = [" + inClass.getName() + "]", e);
        }
    }

    public String getServiceUrl() {
        String serviceUrl = serviceUrlSelector.selectUrl(applicationName);
        if (serviceUrl != null && !serviceUrl.endsWith("/")) {
            serviceUrl = serviceUrl + "/";
        }
        return serviceUrl + exportName;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.serviceUrlSelector == null) {
            throw new IllegalArgumentException("Property \'serviceUrlSelector\' is required");
        }
        if (this.exportName == null) {
            throw new IllegalArgumentException("Property \'exportName\' is required");
        }
        if (this.applicationName == null) {
            throw new IllegalArgumentException("Property \'applicationName\' is required");
        }
    }

    public void setServiceUrlSelector(ServiceUrlSelector serviceUrlSelector) {
        this.serviceUrlSelector = serviceUrlSelector;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    public void setUrl(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setInClass(Class inClass) {
        this.inClass = inClass;
    }

}
