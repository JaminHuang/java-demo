package com.demo.sdk.apollo;

import java.util.HashSet;
import java.util.Set;

/**
 * namespace管理
 */
public class ConfigContext {

    private static Set<String> namespaces = new HashSet<>();

    private static String[] application;

    private static String config;

    public static void setApplication(String[] arr) {
        if (arr == null || arr.length == 0) {
            return;
        }
        application = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            application[i] = arr[i].trim();
            namespaces.add(arr[i].trim());
        }
    }

    public static String[] getApplication() {
        return application;
    }

    public static void setConfig(String str) {
        config = str.trim();
        namespaces.add(str.trim());
    }

    public static String getConfig() {
        return config;
    }

    public static Set<String> getNamespaces() {
        return namespaces;
    }

}
