package com.demo.ibatx.provider.anno;


import com.demo.ibatx.provider.BaseProvider;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DeleteSqlProvider {

    Class<? extends BaseProvider> provider() ;
}
