package com.demo.ibatx.provider.anno;


import com.demo.ibatx.provider.BaseProvider;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface UpdateSqlProvider {

    Class<? extends BaseProvider> provider();

}
