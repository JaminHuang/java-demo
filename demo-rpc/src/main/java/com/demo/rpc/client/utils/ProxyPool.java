package com.demo.rpc.client.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理类池
 */
public class ProxyPool {

    private static final Map<Class<?>, Object> objs = new HashMap<Class<?>, Object>();

    public static void add(Class<?> clazz, Object obj) {
        objs.put(clazz, obj);
    }

    public static Object get(Class<?> clazz) {
        return objs.get(clazz);
    }

}