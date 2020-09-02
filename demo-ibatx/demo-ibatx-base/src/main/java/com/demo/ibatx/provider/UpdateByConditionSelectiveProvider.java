package com.demo.ibatx.provider;

import com.demo.ibatx.core.entity.Condition;
import com.demo.ibatx.sql.builder.SqlBuilder;

public class UpdateByConditionSelectiveProvider extends BaseProvider {
    @Override
    public String produce(ParamProviderContext context) {
        Condition condition = getConditionParam(context.getParameters());
        return SqlBuilder.update(context.getEntityInfo())
                .isSelective(true)
                .entity(getEntityParam(context.getParameters(), context.getEntityInfo().getEntityClass()))
                .where(getConditionParam(context.getParameters()))
                .fields(condition.getFieldsNames())
                .excludeFields(condition.getExcludeFieldsNames())
                .getSql();
    }

}
