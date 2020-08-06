package com.demo.rpc.annotation;

import java.lang.annotation.*;

/**
 * 远程服务client自动注入注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteResource {

    /**
     * 保留属性 用于本地调试 如果配置了url, 则以url为主, 直接对url进行远程调用
     * 例如: @RemoteResource(url = "http://192.168.30.41:7103/examplerpcservice",
     * path="msg") private ExampleService exampleService;
     */
    String url() default "";

    /**
     * 超时时间, 单位为毫秒
     */
    long connectTimeout() default -1;

    /**
     * 超时时间, 单位为毫秒
     */
    long readTimeout() default -1;

}
