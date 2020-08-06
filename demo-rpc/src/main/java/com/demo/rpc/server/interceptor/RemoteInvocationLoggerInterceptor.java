package com.demo.rpc.server.interceptor;

import com.demo.sdk.thread.ReqThreadLocal;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求日志打印
 */
public class RemoteInvocationLoggerInterceptor implements MethodInterceptor {

    private static final Logger defaultLogger = LoggerFactory.getLogger(RemoteInvocationLoggerInterceptor.class);

    private Map<String, Logger> loggerMap = new ConcurrentHashMap<>();

    /**
     * @see MethodInterceptor#invoke(MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Logger logger = null;
        Method method = invocation.getMethod();
        String methodName = method.getName();
        Object[] args = invocation.getArguments();
        if (invocation.getThis() != null) {
            Class clazz = AopUtils.getTargetClass(invocation.getThis());
            if (clazz != null) {
                logger = loggerMap.get(clazz.getSimpleName());
                if (logger == null) {
                    logger = LoggerFactory.getLogger(clazz);
                    loggerMap.put(clazz.getSimpleName(), logger);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-----");
        sb.append(methodName);
        sb.append("-----");
        sb.append(", [tid] = ");
        sb.append(ReqThreadLocal.getTid());
        if (args != null && args.length != 0) {
            sb.append(", [param] = ");
            for (int i = 0; i < args.length; i++) {
                if (!sb.toString().endsWith("[param] = ")) {
                    sb.append(", ");
                }
                sb.append(args[i]);
            }
        }
        logger = logger != null ? logger : defaultLogger;
        logger.info(sb.toString());
        long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long end = System.currentTimeMillis();
            logger.info("{}耗时{}ms", methodName, end - start);
        }
    }
}