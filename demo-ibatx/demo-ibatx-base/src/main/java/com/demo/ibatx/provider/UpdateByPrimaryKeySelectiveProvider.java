package com.demo.ibatx.provider;

import com.demo.ibatx.sql.builder.SqlBuilder;

/**
 * 根据主键更新，并且根据字段更新过滤
 */
public class UpdateByPrimaryKeySelectiveProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext context) {
        return SqlBuilder.update(context.getEntityInfo())
                .isSelective(true)
                .entity(getEntityParam(context.getParameters(), context.getEntityInfo().getEntityClass()))
                .wherePk()
                .getSql();
    }
}
