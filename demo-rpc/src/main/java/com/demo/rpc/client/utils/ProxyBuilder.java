package com.demo.rpc.client.utils;

import com.demo.rpc.client.KylinHessianProxyFactoryBean;
import com.demo.rpc.client.ServiceUrlSelector;
import com.demo.rpc.core.SpringContext;
import com.demo.rpc.hessian.KylinHessianProxyFactory;
import com.demo.rpc.hessian.properties.HessianHttpClientProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.caucho.HessianClientInterceptor;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.util.StringUtils;

/**
 * 使用方法:
 * <p>
 * 创建普通服务代理类:
 * <p>
 * ProxyBuilder.create().setServiceUrl("http://localhost:8080/exampleservice").setServiceExportName("hello").setClazz(HelloService.class).build()
 * <p>
 * 创建http连接池代理类:
 * <p>
 * ProxyBuilder.create() .useHttpClient()
 * .setServiceUrl("http://localhost:8080/exampleservice")
 * .setServiceExportName("hello") .setClazz(HelloService.class) .build();
 *
 * @author <a href="mailto:chenyb@59store.com">山人</a>
 * @version 2.1 16/05/13
 * @since 2.1
 */
public class ProxyBuilder {

    private static final String HTTP = "http";

    private boolean useHttpClient = true;

    private long connectTimeout = -1;

    private long readTimeout = -1;

    private String applicationName;

    private String exportName;

    private Class<?> interfaceClass;

    private Class<?> inClass;

    /**
     * 创建ProxyBuilder
     */
    public static ProxyBuilder create() {
        return new ProxyBuilder();
    }

    protected static void fillProperties(HessianClientInterceptor hessianClientInterceptor, String serviceUrl,
                                         String serviceExportName, Class<?> interfaceClass) {
        hessianClientInterceptor.setOverloadEnabled(true);
        hessianClientInterceptor.setServiceInterface(interfaceClass);
        if (!serviceUrl.endsWith("/")) {
            serviceUrl = serviceUrl + "/";
        }
        hessianClientInterceptor.setServiceUrl(serviceUrl + serviceExportName);
    }

    private void check() {
        if (applicationName == null) {
            throw new IllegalArgumentException("Property 'applicationName' is required");
        }
        if (exportName == null) {
            throw new IllegalArgumentException("Property 'exportName' is required");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Property 'interfaceClass' is required");
        }
    }

    /**
     * 创建远程服务代理对象
     */
    public <T> T build() {
        check();
        if (StringUtils.startsWithIgnoreCase(applicationName, HTTP)) {
            return this.buildNormalHPFBean();
        } else {
            return this.buildKylinHPFBean();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T buildNormalHPFBean() {
        HessianProxyFactoryBean hessianProxyFactoryBean = new HessianProxyFactoryBean();
        fillProperties(hessianProxyFactoryBean, applicationName, exportName, interfaceClass);
        initHessianClientInterceptor(hessianProxyFactoryBean);
        return (T) hessianProxyFactoryBean.getObject();
    }

    @SuppressWarnings("unchecked")
    private <T> T buildKylinHPFBean() {
        ApplicationContext ctx = SpringContext.getApplicationContext();
        ServiceUrlSelector serviceUrlSelector = ctx.getBean(ServiceUrlSelector.class);
        RpcClientNameManager.add(applicationName);
        KylinHessianProxyFactoryBean hessianProxyFactoryBean = new KylinHessianProxyFactoryBean();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        hessianProxyFactoryBean.setHessian2(true);
        hessianProxyFactoryBean.setExportName(exportName);
        hessianProxyFactoryBean.setUrl(applicationName);
        hessianProxyFactoryBean.setServiceInterface(interfaceClass);
        hessianProxyFactoryBean.setInClass(inClass);
        hessianProxyFactoryBean.setServiceUrlSelector(serviceUrlSelector);
        initHessianClientInterceptor(hessianProxyFactoryBean);
        return (T) hessianProxyFactoryBean.getObject();
    }

    private void initHessianClientInterceptor(HessianClientInterceptor interceptor) {
        HessianHttpClientProperties hessianClientConf = null;
        if (SpringContext.getApplicationContext() != null
                && SpringContext.getApplicationContext().containsBean(HessianHttpClientProperties.BEAN_NAME)) {
            hessianClientConf = (HessianHttpClientProperties) SpringContext.getApplicationContext()
                    .getBean(HessianHttpClientProperties.BEAN_NAME);
        }
        if (hessianClientConf == null) {
            hessianClientConf = new HessianHttpClientProperties();
        }
        if (useHttpClient && hessianClientConf.isUseHttpClient()) {
            KylinHessianProxyFactory hessianProxyFactory = new KylinHessianProxyFactory();
            hessianProxyFactory.setOverloadEnabled(true);
            hessianProxyFactory.setHessian2(hessianClientConf.isUseHessian2());
            hessianProxyFactory.setHessianHttpClientProperties(hessianClientConf);
            // Set proxyFactory
            interceptor.setProxyFactory(hessianProxyFactory);
        }
        interceptor.setConnectTimeout(connectTimeout > 0 ? connectTimeout : hessianClientConf.getConnectTimeout());
        interceptor.setReadTimeout(readTimeout > 0 ? readTimeout : hessianClientConf.getReadTimeout());
        interceptor.afterPropertiesSet();
    }

    /**
     * 使用httpclient替代原生httpconnector, 开启http连接池功能
     */
    public ProxyBuilder useHttpClient() {
        useHttpClient = true;
        return this;
    }

    /**
     * 不使用httpclient替代原生httpconnector, 无http连接池功能
     */
    public ProxyBuilder noUseHttpClient() {
        useHttpClient = false;
        return this;
    }

    /**
     * 该配置已失效, 转移至全局配置"kylin.rpc.client.userAgent"
     */
    @Deprecated
    public ProxyBuilder setUserAgent(String userAgent) {
        return this;
    }

    /**
     * 该配置已失效, 转移至全局配置"kylin.rpc.client.systemProperties"
     */
    @Deprecated
    public ProxyBuilder setSystemProperties(boolean systemProperties) {
        return this;
    }

    /**
     * 该配置已失效, 转移至全局配置"kylin.rpc.client.contentCompressionDisabled"
     */
    @Deprecated
    public ProxyBuilder setContentCompressionDisabled(boolean contentCompressionDisabled) {
        return this;
    }

    /**
     * 该配置已失效, 转移至全局配置"kylin.rpc.client.maxConnTotal"
     */
    @Deprecated
    public ProxyBuilder setMaxConnTotal(int maxConnTotal) {
        return this;
    }

    /**
     * @deprecated replace by {@link #setApplicationName(String)}
     */
    @Deprecated
    public ProxyBuilder setServiceName(String serviceName) {
        this.setApplicationName(serviceName);
        return this;
    }

    /**
     * applicationName支持：
     * <ol>
     * <li>服务名，支持负载均衡，推荐使用</li>
     * <li>正常URL（以http开头），不支持负载均衡，通常用于测试使用</li>
     * </ol>
     */
    public ProxyBuilder setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    /**
     * RPC暴露的服务名, 设置成@RemotingService中的exportPath
     */
    public ProxyBuilder setExportName(String exportName) {
        this.exportName = exportName;
        return this;
    }

    public ProxyBuilder setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return this;
    }

    public ProxyBuilder setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public ProxyBuilder setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public ProxyBuilder setInClass(Class inClass){
        this.inClass = inClass;
        return this;
    }

    // @Deprecated
    // public static Object buildProxyService(String serviceUrl, String
    // serviceExportName, Class clazz) {
    // return
    // ProxyBuilder.create().setServiceExportName(serviceExportName).setServiceUrl(serviceUrl).setInterfaceClass(clazz).build();
    // }

}
