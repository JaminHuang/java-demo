package com.demo.sdk.exception;

/**
 * http请求拒绝异常
 * web服务重启关闭接受http请求后与完成重启之间接受到的请求
 */
public class HttpRefusedException extends RuntimeException {

    public HttpRefusedException() {
        super();
    }

    public HttpRefusedException(String message) {
        super(message);
    }

}

