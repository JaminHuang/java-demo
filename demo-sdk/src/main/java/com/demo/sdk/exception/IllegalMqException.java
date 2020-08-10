package com.demo.sdk.exception;

/**
 * 非法mq异常
 */
public class IllegalMqException extends RuntimeException {

    public IllegalMqException(String message) {
        super(message);
    }
}
