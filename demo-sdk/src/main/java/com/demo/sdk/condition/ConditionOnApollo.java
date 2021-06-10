package com.demo.sdk.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 比较配置中心的值是否满足条件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnApolloCondition.class)
public @interface ConditionOnApollo {

    String key();

    String value() default "";

    Compare compare() default Compare.EQUAL;

}
