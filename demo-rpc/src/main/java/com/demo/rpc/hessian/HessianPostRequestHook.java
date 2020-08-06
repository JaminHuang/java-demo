package com.demo.rpc.hessian;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

public abstract class HessianPostRequestHook {
    public abstract void invoke(HttpPost requestEntity, HttpResponse responseEntity);
}
