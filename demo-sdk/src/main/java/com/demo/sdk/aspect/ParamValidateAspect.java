package com.demo.sdk.aspect;

import com.demo.sdk.annotation.ParamValidate;
import com.demo.sdk.exception.ServiceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangpu
 * @date 2019-07-26 10:13
 */
@Aspect
@Component
public class ParamValidateAspect {

    private static final String NULL_MESSAGE = "Required parameter '%s' is null";

    private static final String EMPTY_MESSAGE = "Required parameter '%s' is empty";

    private static final String LENGHT_MESSAGE = "Required parameter '%s' length limit %s,but now is %s";

    Logger logger = LoggerFactory.getLogger(ParamValidateAspect.class);

    @Pointcut("@annotation(com.demo.sdk.annotation.ParamValidate))")
    public void paramValidate() {
    }

    @Pointcut("paramValidate()")
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

        if (null == parameterNames || parameterNames.length == 0) {
            return;
        }
        //获得参数
        Object[] args = joinPoint.getArgs();
        Map<String, Object> paramMap = new HashMap<>(16);

        // 组装参数map
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }

        if (method.isAnnotationPresent(ParamValidate.class)) {
            ParamValidate paramValidate = method.getAnnotation(ParamValidate.class);
            String[] nonNullValues = paramValidate.notNull();
            String[] nonEmptyValues = paramValidate.notEmpty();

            ExpressionParser parser = new SpelExpressionParser();
            // 表达式上下文
            EvaluationContext context = new StandardEvaluationContext();
            // 为了让表达式可以访问该对象, 先把对象放到上下文中
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }

            // null校验
            if (null != nonNullValues && nonNullValues.length > 0) {
                for (String e : nonNullValues) {
                    Object value = parser.parseExpression(e.trim()).getValue(context, Object.class);
                    if (Objects.isNull(value)) {
                        throw new ServiceException(String.format(NULL_MESSAGE, e));
                    }
                }
            }

            // 空字符串校验
            if (null != nonEmptyValues && nonEmptyValues.length > 0) {
                for (String e : nonEmptyValues) {
                    Object value = parser.parseExpression(e.trim()).getValue(context, Object.class);
                    checkNotEmpty(e, value);
                }
            }
        }
    }


    /**
     * 判断是否非空及长度限制
     */
    private static void checkNotEmpty(String paraName, Object paraValue) {
        if (Objects.isNull(paraValue)) {
            throw new ServiceException(String.format(EMPTY_MESSAGE, paraName));
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
            throw new ServiceException(String.format(EMPTY_MESSAGE, paraName));
        }
    }

}
