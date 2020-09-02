package com.demo.ibatx.provider;

import com.demo.ibatx.sql.builder.SqlBuilder;

/**
 * 根据主键查询
 */
public class SelectByPrimaryKeyProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {


        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .wherePk()
                .getSql();
    }



}
