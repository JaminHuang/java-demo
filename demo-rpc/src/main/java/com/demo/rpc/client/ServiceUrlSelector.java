package com.demo.rpc.client;

import com.demo.rpc.client.exceptions.NoServiceUrlFoundException;

public interface ServiceUrlSelector {
    String selectUrl(String appName) throws NoServiceUrlFoundException;
}
