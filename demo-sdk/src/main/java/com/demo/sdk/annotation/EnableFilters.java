package com.demo.sdk.annotation;

import com.demo.sdk.filter.FiltersRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开始自定义的过滤器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FiltersRegistrar.class)
@Documented
@Inherited
public @interface EnableFilters {

}

