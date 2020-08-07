package com.demo.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法参数校验
 * 使用方法：@ParamValidate(notNull = {"#dto.name", "#dto.age", "#dto.desc", "#name", "#dto.subDto.name"},
 * notEmpty = {"#dto.name", "#dto.age", "#dto.desc", "#name", "#dto.subDto.name"})
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamValidate {

    /**
     * 非null判断
     */
    String[] notNull() default {};

    /**
     * 非空字符判断
     *
     * @return
     */
    String[] notEmpty() default {};
}
