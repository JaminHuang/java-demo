package com.demo.rpc.support;

public class RpcThreadLocal {

    private final static ThreadLocal<String> serviceUrl = ThreadLocal.withInitial(() -> "");

    public static void removeAll() {
        serviceUrl.remove();
    }

    public static String getServiceUrl() {
        return serviceUrl.get();
    }

    public static void setServiceUrl(String url) {
        serviceUrl.set(url);
    }

}
