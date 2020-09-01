package com.demo.sdk.aspect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.sdk.annotation.Permission;
import com.demo.sdk.annotation.Role;
import com.demo.sdk.consts.RoleEnum;
import com.demo.sdk.exception.BizException;
import com.demo.sdk.exception.ParaException;
import com.demo.sdk.response.Result;
import com.demo.sdk.thread.ReqThreadLocal;
import org.apache.poi.ss.formula.functions.T;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Order(2)
@Component
@EnableAspectJAutoProxy(exposeProxy = true)
public abstract class PermissionAspect {

    private static final String NOT_ROLE = "YOU HAVE NOT THIS ROLE:%s";

    private static final String NOT_PERMISSION = "YOU HAVE NOT PERMISSION:%s";

    @SuppressWarnings("unchecked")
    @Around("@annotation(com.demo.sdk.annotation.Permission)")
    public Result<T> aroundHasPermission(ProceedingJoinPoint point) throws Throwable {
        // 访问这个方法必须首先去校验token 如果有的话就去查询其身份信息 角色权限
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        String accesstoken = request.getHeader("access-token");
        if (accesstoken == null) {
            throw new BizException("您尚未登录，请先登陆");
        }
        JSONObject userAdmVO = ReqThreadLocal.getUser();
        if (userAdmVO == null) {
            throw new BizException("访问拒绝，请通过登陆后操作！");
        }
        Permission hasPermission = method.getAnnotation(Permission.class);
        String[] hasPermissionsValue = hasPermission.value();
        if (hasPermissionsValue != null) {
            JSONArray array = (JSONArray) userAdmVO.get("permissions");
            if (array == null) {
                throw new BizException("访问拒绝，您的账户状态有异常，请联系管理员处理");
            }
            Set<String> permissions = new HashSet<>();
            for (int i = 0; i < array.size(); i++) {
                String permission = array.getString(i);
                permissions.add(permission);
            }

            for (int i = 0; i < hasPermissionsValue.length; i++) {
                String paraName = hasPermissionsValue[i];
                if (!permissions.contains(paraName)) {
                    throw new ParaException(String.format(NOT_PERMISSION, paraName));
                }
            }
        }
        return (Result<T>) point.proceed();
    }

    @SuppressWarnings("unchecked")
    @Around("@annotation(com.demo.sdk.annotation.Role)")
    public Result<T> aroundHasRole(ProceedingJoinPoint point) throws Throwable {
        // 访问这个方法必须首先去校验token 如果有的话就去查询其身份信息 角色权限
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        String accesstoken = request.getHeader("access-token");
        if (accesstoken == null) {
            throw new BizException("您尚未登录，请先登陆");
        }
        JSONObject userAdmVO = ReqThreadLocal.getUser();
        if (userAdmVO == null) {
            throw new BizException("访问拒绝，请通过登陆后操作！");
        }
        Role hasRole = method.getAnnotation(Role.class);
        RoleEnum[] hasRolesValue = hasRole.value();

        if (hasRolesValue != null) {
            JSONArray array = (JSONArray) userAdmVO.get("roleInfo");
            if (array == null) {
                throw new BizException("访问拒绝，您的账户状态有异常，请联系管理员处理");
            }
            Set<String> roles = new HashSet<>();
            for (int i = 0; i < array.size(); i++) {
                String role = array.getString(i);
                roles.add(role);
            }

            for (int i = 0; i < hasRolesValue.length; i++) {
                RoleEnum paraName = hasRolesValue[i];
                if (!roles.contains(paraName.toString())) {
                    throw new ParaException(String.format(NOT_ROLE, paraName));
                }
            }
        }
        return (Result<T>) point.proceed();
    }

}
