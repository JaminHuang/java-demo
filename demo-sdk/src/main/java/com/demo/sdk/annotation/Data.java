package com.demo.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 过滤 Result中成员变量data中的成员变量 二者选一，同时存在只处理include
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {
	/**
	 * 返回只包含
	 */
	String[] include() default {};

	/**
	 * 返回不包含
	 */
	String[] exclude() default {};
}
