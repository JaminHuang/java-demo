package com.demo.ibatx.provider;


import com.demo.ibatx.sql.builder.SqlBuilder;

/**
 * 根据
 */
public class UpdateByPrimaryKeyProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext context) {
        return SqlBuilder.update(context.getEntityInfo())
                .isSelective(false)
                .entity(getEntityParam(context.getParameters(), context.getEntityInfo().getEntityClass()))
                .wherePk()
                .getSql();
    }

}
