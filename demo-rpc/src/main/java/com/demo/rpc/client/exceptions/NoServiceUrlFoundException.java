package com.demo.rpc.client.exceptions;

public class NoServiceUrlFoundException extends RuntimeException {

    private static final long serialVersionUID = -9019269915531937656L;

    public NoServiceUrlFoundException() {
        super();
    }

    public NoServiceUrlFoundException(String message) {
        super(message);
    }

    public NoServiceUrlFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoServiceUrlFoundException(Throwable cause) {
        super(cause);
    }

}
