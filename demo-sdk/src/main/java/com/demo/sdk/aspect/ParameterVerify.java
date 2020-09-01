package com.demo.sdk.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.sdk.annotation.NotEmpty;
import com.demo.sdk.annotation.NotNull;
import com.demo.sdk.exception.ParaException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ParameterVerify {

    private static final String NULL_MESSAGE = "Required parameter '%s' is null";

    private static final String EMPTY_MESSAGE = "Required parameter '%s' is empty";

    private static final String LENGHT_MESSAGE = "Required parameter '%s' length limit %s,but now is %s";

    /**
     * 检查参数
     */
    static void verify(JoinPoint joinPoint) throws IllegalAccessException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        String[] notEmptyValue = null;
        int[] notEmptyMaxLen = null;
        String[] notNullValue = null;

        if (method.isAnnotationPresent(NotEmpty.class)) {
            NotEmpty notEmpty = method.getAnnotation(NotEmpty.class);
            notEmptyValue = notEmpty.value();
            notEmptyMaxLen = notEmpty.maxLen();
        }

        if (method.isAnnotationPresent(NotNull.class)) {
            NotNull notNull = method.getAnnotation(NotNull.class);
            notNullValue = notNull.value();
        }

        // post,delete,put 请求体
        JSONObject body = null;

        // 被Body注释的参数对象
        Class<?> requestBodyedClazz = getClassByAnnotaion(method, RequestBody.class);
        Object requestBodyedObject = null;
        if (requestBodyedClazz != null) {
            Object[] args = joinPoint.getArgs();
            body = new JSONObject();
            for (Object arg : args) {
                if (arg != null && arg.getClass().equals(requestBodyedClazz)) {
                    requestBodyedObject = arg;
                    if (requestBodyedObject != null) {
                        body.putAll((JSONObject) JSON.toJSON(requestBodyedObject));
                    }
                    break;
                }
            }
        }

        // NotNull校验
        if (notNullValue != null) {
            for (int i = 0; i < notNullValue.length; i++) {
                String paraName = notNullValue[i];
                Object paraValue = null;
                // 获取值
                if (requestBodyedObject != null) {
                    paraValue = getValueByField(requestBodyedObject, paraName);
                } else {
                    paraValue = request.getParameter(paraName);
                }
                if (paraValue == null) {
                    throw new ParaException(String.format(NULL_MESSAGE, paraName));
                }
            }
        }

        // NotEmpty校验
        if (notEmptyValue != null) {
            for (int i = 0; i < notEmptyValue.length; i++) {
                String paraName = notEmptyValue[i];
                Object paraValue = null;
                // 获取值
                if (requestBodyedObject != null) {
                    paraValue = getValueByField(requestBodyedObject, paraName);
                } else {
                    paraValue = request.getParameter(paraName);
                }

                int maxLen = Integer.MAX_VALUE;
                if (notEmptyMaxLen != null && i < notEmptyMaxLen.length) {
                    maxLen = notEmptyMaxLen[i];
                }
                checkNotEmpty(paraName, paraValue, maxLen);
            }
        }

    }

    /**
     * 获取对象某个属性的值
     */
    private static Object getValueByField(Object object, String field)
            throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Set<Field> fieldSet = getDeclaredFields(clazz);
        for (Field f : fieldSet) {
            f.setAccessible(true);
            if (f.getName().equals(field)) {
                return f.get(object);
            }
        }
        return null;
    }

    /**
     * 获取class所有成员属性
     */
    private static Set<Field> getDeclaredFields(Class<?> clazz) {
        Set<Field> fieldSet = new HashSet<>();
        while (clazz != null) {
            fieldSet.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldSet;
    }

    /**
     * 获取被RequestBody注解的方法参数的class
     */
    private static Class<?> getClassByAnnotaion(Method method, Class<? extends Annotation> clazz) {
        Parameter[] params = method.getParameters();
        for (Parameter e : params) {
            if (e.isAnnotationPresent(clazz)) {
                return e.getType();
            }
        }
        return null;
    }

    /**
     * 判断是否非空及长度限制
     */
    private static void checkNotEmpty(String paraName, Object paraValue, int maxLen) {
        if (paraValue == null) {
            throw new ParaException(String.format(EMPTY_MESSAGE, paraName));
        }

        int paraLen = 0;

        if (paraValue instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) paraValue;
            paraLen = collection.size();
        } else if (paraValue instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) paraValue;
            paraLen = map.size();
        } else if (paraValue.getClass().isArray()) {
            Object[] array = (Object[]) paraValue;
            paraLen = array.length;
        } else {
            paraLen = String.valueOf(paraValue).length();
        }

        if (paraLen == 0) {
            throw new ParaException(String.format(EMPTY_MESSAGE, paraName));
        }

        if (paraLen > maxLen) {
            throw new ParaException(String.format(LENGHT_MESSAGE, paraName, maxLen, paraLen));
        }

    }

}
