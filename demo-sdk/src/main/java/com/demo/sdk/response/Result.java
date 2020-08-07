package com.demo.sdk.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.demo.sdk.consts.BErrorCode;
import com.demo.sdk.consts.IErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * API返回类
 */
@Data
public class Result<T> implements Serializable {

    private final static long serialVersionUID = 1L;
    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误提示
     */
    private String errorMessage;
    /**
     * 数据
     */
    private T data;
    /**
     * 总数
     */
    private Integer total;
    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date currentTime = new Date();
    /**
     * 当前时间戳
     */
    private Long currentTimestamp = System.currentTimeMillis();
    /**
     * 额外数据
     */
    private Object attach;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setErrorCode(BErrorCode.OK.getCode());
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(T data, int total) {
        Result<T> result = success(data);
        result.setTotal(total);
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<T>();
        result.setErrorCode(BErrorCode.UNDEFINE_ERROR.getCode());
        result.setErrorMessage(BErrorCode.UNDEFINE_ERROR.getMessage());
        return result;
    }

    public static <T> Result<T> fail(String errorMessag) {
        Result<T> result = fail();
        result.setErrorMessage(errorMessag);
        return result;
    }

    public static <T> Result<T> fail(IErrorCode errorCode) {
        Result<T> result = fail();
        result.setErrorCode(errorCode.getCode());
        result.setErrorMessage(errorCode.getMessage());
        return result;
    }

    public static <T> Result<T> fail(IErrorCode errorCode, String description) {
        Result<T> result = fail();
        result.setErrorCode(errorCode.getCode());
        result.setErrorMessage(description);
        return result;
    }

    public boolean isSuccess() {
        return BErrorCode.OK.getCode() == errorCode;
    }

    @Override
    public String toString() {
        return "Result [errorCode=" + errorCode + ", errorMessage=" + errorMessage + ", data=" + data + ", total="
                + total + ", attach=" + attach + "]";
    }
}