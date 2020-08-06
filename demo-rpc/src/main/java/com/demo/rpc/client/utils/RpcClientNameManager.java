package com.demo.rpc.client.utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClientNameManager {
    /**
     * 线程安全
     * 防止内存泄漏
     */
    private static Set<String> names = ConcurrentHashMap.newKeySet();

    public static void add(String name) {
        names.add(name);
    }

    public static Set<String> getNames() {
        return names;
    }

}