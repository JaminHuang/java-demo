package com.demo.rpc.server.interceptor;

import com.demo.sdk.exception.HttpRefusedException;
import com.demo.sdk.support.WebContainer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 远程调用监控类，当项目重启时往上层抛出异常
 */
public class RemoteInvocationMonitorInterceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (WebContainer.isRuning()) {
            WebContainer.incrementAndGet();
            try {
                return invocation.proceed();
            } finally {
                WebContainer.decrementAndGet();
            }
        }
        // 如果web容器在重启中，则抛出Http拒绝异常
        throw new HttpRefusedException("server is stoping");
    }

}
