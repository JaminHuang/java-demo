package com.demo.rpc.server.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

/**
 * 捕捉远程调用异常.
 */
public class RemoteInvocationExceptionInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RemoteInvocationExceptionInterceptor.class);

    private final String exporterName;

    /**
     * Create a new RemoteInvocationExceptionInterceptor.
     */
    public RemoteInvocationExceptionInterceptor() {
        this.exporterName = "";
    }

    /**
     * Create a new RemoteInvocationExceptionInterceptor.
     *
     * @param exporterName the name of the remote exporter (to be used as context
     *                     information in log messages)
     */
    public RemoteInvocationExceptionInterceptor(String exporterName) {
        this.exporterName = exporterName;
    }

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable ex) {
            logger.error("Processing of {} remote call resulted in exception: {}, arguments: {}", exporterName,
                    ClassUtils.getQualifiedMethodName(invocation.getMethod()), invocation.getArguments(), ex);
            throw ex;
        }
    }

}