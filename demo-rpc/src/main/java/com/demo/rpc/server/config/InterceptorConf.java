package com.demo.rpc.server.config;

import com.demo.rpc.server.interceptor.RemoteInvocationLoggerInterceptor;
import com.demo.rpc.server.interceptor.RemoteInvocationMonitorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

@Configuration
public class InterceptorConf {

    public static final String REMOTE_INVOCATION_MONITOR_INTERCEPTOR_NAME = "remoteInvocationMonitorInterceptor";
    public static final String REMOTE_INVOCATION_LOGGER_INTERCEPTOR_NAME = "remoteInvocationLoggerInterceptor";

    @Bean
    @ConditionalOnMissingBean(name = "remoteInvocationMonitorInterceptor")
    public MethodInterceptor remoteInvocationMonitorInterceptor() throws UnknownHostException {
        return new RemoteInvocationMonitorInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(name = "remoteInvocationLoggerInterceptor")
    public MethodInterceptor remoteInvocationLoggerInterceptor() throws UnknownHostException {
        return new RemoteInvocationLoggerInterceptor();
    }

}