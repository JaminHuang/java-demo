package com.demo.ibatx.provider;

import com.demo.ibatx.core.entity.Condition;
import com.demo.ibatx.sql.builder.SqlBuilder;

public class UpdateByConditionProvider extends BaseProvider {


    @Override
    public String produce(ParamProviderContext context) {
        Condition condition = getConditionParam(context.getParameters());
        return SqlBuilder.update(context.getEntityInfo())
                .isSelective(false)
                .where(getConditionParam(context.getParameters()))
                .entity(getEntityParam(context.getParameters(), context.getEntityInfo().getEntityClass()))
                .fields(condition.getFieldsNames())
                .excludeFields(condition.getExcludeFieldsNames())
                .getSql();
    }


}
