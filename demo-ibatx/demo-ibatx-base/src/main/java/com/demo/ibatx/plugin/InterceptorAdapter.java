package com.demo.ibatx.plugin;

import org.apache.ibatis.mapping.MappedStatement;

import java.util.Map;

/**
 * 拦截器适配器
 */
public interface InterceptorAdapter {

    /**
     * 拦截方法和参数
     *
     * @param mappedStatement mybatis-mappedStatement
     * @param mapperMethodParam 参数
     * @return 是否拦截 true - 不拦截 ；false - 拦截器
     */
    boolean intercept(MappedStatement mappedStatement, Map<String, Object> mapperMethodParam);
}
