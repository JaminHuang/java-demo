package com.demo.rpc.hessian;

import com.sun.deploy.net.HttpResponse;
import org.apache.http.client.methods.HttpPost;

public abstract class HessianPostRequestHook {
    public abstract void invoke(HttpPost requestEntity, HttpResponse responseEntity);
}
