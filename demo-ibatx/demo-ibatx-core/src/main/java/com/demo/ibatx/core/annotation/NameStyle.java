package com.demo.ibatx.core.annotation;


/**
 * 表字段命名规则
 */
public enum NameStyle {

    /**
     * 原值
     */
    normal,
    /**
     * 驼峰转下划线
     */
    camelhump,
    /**
     * 转换为大写
     */
    uppercase,
    /**
     * 转换为小写
     */
    lowercase,
    /**
     * 驼峰转下划线大写形式
     */
    camelhumpAndUppercase,
    /**
     * 驼峰转下划线小写形式
     */
    camelhumpAndLowercase;

}
