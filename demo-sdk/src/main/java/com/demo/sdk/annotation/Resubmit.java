package com.demo.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 避免重复提交
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resubmit {

    /**
     * 指定时间内不可重复提交,单位秒
     *
     * @return
     */
    int timeout() default 3;

    /**
     * 描述
     *
     * @return
     */
    String description() default "请勿重复提交";

}