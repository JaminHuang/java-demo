package com.demo.sdk.aspect;

import com.alibaba.fastjson.JSONObject;
import com.demo.sdk.annotation.AccessToken;
import com.demo.sdk.consts.BErrorCode;
import com.demo.sdk.exception.BizException;
import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class AccessTokenVerify {

    static void verify(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        boolean tokenRequired = false;

        Class<?> clazz = method.getDeclaringClass();
        AccessToken accessTokenAnnotation = null;
        // 先获取类注解
        if (clazz.isAnnotationPresent(AccessToken.class)) {
            accessTokenAnnotation = (AccessToken) clazz.getAnnotation(AccessToken.class);
            tokenRequired = accessTokenAnnotation.required();
        }
        // 再获取方法注解
        if (method.isAnnotationPresent(AccessToken.class)) {
            accessTokenAnnotation = method.getAnnotation(AccessToken.class);
            tokenRequired = accessTokenAnnotation.required();
        }
        if (!tokenRequired) {
            return;
        }

        String accessToken = request.getHeader("access-token");
        //token为空
        if (StringUtils.isEmpty(accessToken)) {
            throw new BizException(BErrorCode.ACCESS_TOKEN_ERROR);
        }
        JSONObject user = ReqThreadLocal.getUser();
        if (user == null) {
             throw new BizException(BErrorCode.ACCESS_TOKEN_ERROR);
        }
    }
}
