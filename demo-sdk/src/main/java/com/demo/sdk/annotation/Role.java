package com.demo.sdk.annotation;

import com.demo.sdk.consts.RoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Role {
	/**
	 * 参数
	 */
	RoleEnum[] value();

	/**
	 * 参数最大长度,对应value字段的顺序，不填默认不限制
	 */
	int[] maxLen() default {};
}
