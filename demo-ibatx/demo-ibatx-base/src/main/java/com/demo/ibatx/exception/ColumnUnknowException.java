package com.demo.ibatx.exception;

/**
 * 未知的字段异常
 */
public class ColumnUnknowException extends RuntimeException {

    public ColumnUnknowException(String message) {
        super(message);
    }

    public ColumnUnknowException(String message, Throwable cause) {
        super(message, cause);
    }
}
