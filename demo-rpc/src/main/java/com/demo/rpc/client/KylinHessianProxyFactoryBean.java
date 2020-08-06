package com.demo.rpc.client;

import org.springframework.aop.framework.ProxyFactory;

public class KylinHessianProxyFactoryBean extends KylinHessianClientInterceptor {

    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
    }

    public Object getObject() {
        return this.serviceProxy;
    }

}