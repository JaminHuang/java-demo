package com.demo.sdk.exception;

/**
 * 参数缺省异常
 */
public class ParaException extends RuntimeException {

	private final static long serialVersionUID = 1L;

	public ParaException() {

	}

	public ParaException(String message) {
		super(message);
	}

}
