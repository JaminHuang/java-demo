package com.demo.sdk.exception;

/**
 * 非法对象异常
 */
public class IllegalObjectException extends RuntimeException {

    public IllegalObjectException(String message) {
        super(message);
    }

    public static IllegalObjectException getInstance(String message) {
        return new IllegalObjectException(message);
    }

    public static IllegalObjectException getInstance(String message, Object... args) {
        for (Object obj : args) {
            if (obj == null) {
                continue;
            }
            message = message.replaceFirst("\\{\\}", String.valueOf(obj));
        }
        return new IllegalObjectException(message);
    }
}