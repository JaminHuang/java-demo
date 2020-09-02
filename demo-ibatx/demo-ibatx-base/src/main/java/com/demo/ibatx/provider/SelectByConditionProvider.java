package com.demo.ibatx.provider;

import com.demo.ibatx.core.entity.Condition;
import com.demo.ibatx.sql.builder.SqlBuilder;

public class SelectByConditionProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        Condition condition = getConditionParam(paramProviderContext.getParameters());
        return SqlBuilder.select(paramProviderContext.getEntityInfo())
                .fields(condition.getFieldsNames())
                .excludeFields(condition.getExcludeFieldsNames())
                .where(condition)
                .limit(condition.getLimitCondition())
                .forceMaster(condition.isforceMaster())
                .getSql();
    }

}
