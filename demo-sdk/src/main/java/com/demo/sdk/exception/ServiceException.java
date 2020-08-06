package com.demo.sdk.exception;

import com.demo.sdk.consts.IErrorCode;

/**
 * 业务异常
 */
public class ServiceException extends RuntimeException {
    /**
     *
     */
    private final static long serialVersionUID = 5827674159912277791L;

    private int code = 101;

    public ServiceException() {

    }

    public ServiceException(String message) {
        super(message);
    }

    public static ServiceException getInstance(String message) {
        return new ServiceException(message);
    }

    public static ServiceException getInstance(String message, Object... args) {
        for (Object obj : args) {
            if (obj == null) {
                continue;
            }
            message = message.replaceFirst("\\{\\}", String.valueOf(obj));
        }
        return new ServiceException(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(IErrorCode errorCode) {
        this(errorCode, errorCode != null ? errorCode.getMessage() : "unknown");
    }

    public ServiceException(IErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public IErrorCode getErrorCode() {
        String message = this.getMessage();
        return new IErrorCode() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

}
