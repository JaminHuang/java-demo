package com.demo.ibatx.provider;

import com.demo.ibatx.sql.builder.SqlBuilder;

public class LogicDeleteByPrimaryKeyProvider extends BaseProvider {
    @Override
    public String produce(ParamProviderContext paramProviderContext) {

        return SqlBuilder.delete(paramProviderContext.getEntityInfo())
                .wherePk()
                .logicDelete()
                .getSql();
    }
}
