package com.demo.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
    /**
     * key
     *
     * @param
     * @author caiLinFeng
     * @date 2018年11月27日
     */
    String value() default "";
}
