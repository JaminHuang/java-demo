package com.demo.rpc.annotation;

import java.lang.annotation.*;

/**
 * 远程服务接口注解, 用于定义rpc默认配置, 以及申明rpc
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Remote {

    /**
     * 服务端建议客户端默认超时时间, 单位为毫秒
     */
    long connectTimeout() default -1;

    /**
     * 服务端建议客户端默认超时时间, 单位为毫秒
     */
    long readTimeout() default -1;

    /**
     * 服务名
     */
    String applicationName() default "";
}
