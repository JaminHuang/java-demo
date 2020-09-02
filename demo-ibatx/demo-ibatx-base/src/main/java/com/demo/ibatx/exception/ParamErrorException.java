package com.demo.ibatx.exception;


/**
 * 参数异常
 */
public class ParamErrorException extends RuntimeException {

    public ParamErrorException() {
    }

    public ParamErrorException(String message) {
        super(message);
    }

    public ParamErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
