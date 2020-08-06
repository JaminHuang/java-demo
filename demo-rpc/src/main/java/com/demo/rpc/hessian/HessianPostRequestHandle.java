package com.demo.rpc.hessian;

import com.sun.deploy.net.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;

public class HessianPostRequestHandle {
    private static ArrayList<HessianPostRequestHook> postRequestHooks = new ArrayList<HessianPostRequestHook>();

    public static void postRequestHandle(HttpPost requestEntity, HttpResponse responseEntity) {
        for (HessianPostRequestHook postQequestHook : postRequestHooks) {
            postQequestHook.invoke(requestEntity, responseEntity);
        }
    }

    public static void addPostRequestHook(HessianPostRequestHook postRequestHook) {
        postRequestHooks.add(postRequestHook);
    }
}
