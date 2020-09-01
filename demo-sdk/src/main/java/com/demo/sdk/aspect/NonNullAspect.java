package com.demo.sdk.aspect;

import com.demo.sdk.annotation.NonNull;
import com.demo.sdk.exception.ServiceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class NonNullAspect {

    private static final String NULL_MESSAGE = "Required parameter '%s' is null";

    @Pointcut("@annotation(com.demo.sdk.annotation.NonNull))")
    public void nonNull() {
    }

    @Pointcut("nonNull()")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        //获得目标实现类的方法
        Method method = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        // 获得参数名称
        String[] parameterNames = pnd.getParameterNames(method);
        //获得参数
        Object[] args = joinPoint.getArgs();
        Map<String, Object> paramMap = new HashMap<>(16);
        // 组装参数map
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }
        if (method.isAnnotationPresent(NonNull.class)) {
            NonNull nonNull = method.getAnnotation(NonNull.class);
            String[] nonNullValues = nonNull.value();
            Arrays.stream(nonNullValues).forEach(e -> {
                Optional.ofNullable(paramMap.get(e)).orElseThrow(() -> new ServiceException(String.format(NULL_MESSAGE, e)));

            });
        }
    }

}
