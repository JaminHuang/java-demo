package com.demo.sdk.apollo;

import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MyConfigRegistrar.class)
public @interface EnableMyConfig {
    /**
     * The order of the apollo config, default is {@link Ordered#LOWEST_PRECEDENCE}, which is Integer.MAX_VALUE.
     * If there are properties with the same name in different apollo configs, the apollo config with smaller order wins.
     *
     * @return
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}
