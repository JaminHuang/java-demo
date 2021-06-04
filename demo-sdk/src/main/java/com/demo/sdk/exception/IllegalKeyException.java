package com.demo.sdk.exception;

/**
 * 非法key异常
 */
public class IllegalKeyException extends RuntimeException {

    public IllegalKeyException(String message) {
        super(message);
    }
}
