package com.demo.ibatx.provider;

import com.demo.ibatx.sql.builder.SqlBuilder;

/**
 * 根据condition查询数量
 */
public class CountByConditionProvider extends BaseProvider {
    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .where(getConditionParam(paramProviderContext.getParameters()))
                .count()
                .getSql();
    }

}
