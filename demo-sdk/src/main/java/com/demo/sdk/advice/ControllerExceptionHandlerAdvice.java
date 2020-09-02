package com.demo.sdk.advice;

import com.demo.sdk.consts.BErrorCode;
import com.demo.sdk.consts.IErrorCode;
import com.demo.sdk.exception.BizException;
import com.demo.sdk.exception.ParaException;
import com.demo.sdk.exception.ServiceException;
import com.demo.sdk.response.Result;
import com.demo.sdk.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * controller异常处理切面
 */
@RestControllerAdvice
public class ControllerExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandlerAdvice.class);

    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        // Result errorMessage
        String errorMessage = "server error";
        IErrorCode errorCode = null;

        if (e instanceof NoHandlerFoundException || e instanceof HttpRequestMethodNotSupportedException) {
        } else if (e instanceof ParaException) {
            ParaException targetEx = (ParaException) e;
            errorMessage = targetEx.getMessage();
        } else if (e instanceof BizException) {
            BizException targetEx = (BizException) e;
            errorCode = targetEx.getErrorCode();
            errorMessage = targetEx.getMessage();
        } else if (e instanceof ServiceException) {
            ServiceException targetEx = (ServiceException) e;
            errorCode = targetEx.getErrorCode();
            errorMessage = targetEx.getMessage();
        } else if (e instanceof DuplicateKeyException) {
            errorCode = BErrorCode.DUPLICATE_KEY_ERROR;
            errorMessage = errorCode.getMessage();
        } else if (e instanceof IllegalArgumentException) {
            IllegalArgumentException targetEx = (IllegalArgumentException) e;
            errorMessage = targetEx.getMessage();
        } else if (e instanceof HttpMessageNotReadableException || e instanceof BindException) {
            errorMessage = "参数错误";
        }

        String exceptionMsg = ExceptionUtils.getExceptionMsg(e);

        logger.error("{}", exceptionMsg);

        if (errorCode != null) {
            return Result.fail(errorCode, errorMessage);
        } else {
            return Result.fail(errorMessage);
        }
    }
}