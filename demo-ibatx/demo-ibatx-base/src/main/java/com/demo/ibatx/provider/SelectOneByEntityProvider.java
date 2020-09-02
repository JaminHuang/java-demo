package com.demo.ibatx.provider;

import com.demo.ibatx.sql.builder.SqlBuilder;

public class SelectOneByEntityProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .where(getEntityParam(paramProviderContext.getParameters(), paramProviderContext.getEntityInfo().getEntityClass()))
                .limit(0, 1)
                .getSql();
    }

}
