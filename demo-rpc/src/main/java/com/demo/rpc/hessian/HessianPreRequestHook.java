package com.demo.rpc.hessian;

import org.apache.http.client.methods.HttpPost;

public abstract class HessianPreRequestHook {
    public abstract void invoke(HttpPost requestEntity);
}
