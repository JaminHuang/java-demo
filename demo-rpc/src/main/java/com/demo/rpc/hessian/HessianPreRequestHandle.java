package com.demo.rpc.hessian;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;

public class HessianPreRequestHandle {
    private static ArrayList<HessianPreRequestHook> preRequestHooks = new ArrayList<HessianPreRequestHook>();

    public static void preRequestHandle(HttpPost requestEntity) {
        for (HessianPreRequestHook preQequestHook : preRequestHooks) {
            preQequestHook.invoke(requestEntity);
        }
    }

    public static ArrayList<HessianPreRequestHook> getPreRequestHooks() {
        return preRequestHooks;
    }

    public static void addPreRequestHook(HessianPreRequestHook preRequestHook) {
        preRequestHooks.add(preRequestHook);
    }
}
