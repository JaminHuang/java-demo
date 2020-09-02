package com.demo.ibatx.sql.builder.condition;

import com.demo.ibatx.core.entity.LimitCondition;
import com.demo.ibatx.meta.EntityInfo;
import com.demo.ibatx.sql.builder.CommonBuilder;

public class LimitSqlBuilder extends CommonBuilder {

    private LimitCondition limitCondition;

    protected LimitSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public LimitSqlBuilder(EntityInfo entityInfo, LimitCondition limitCondition) {
        super(entityInfo);
        this.limitCondition = limitCondition;
    }

    @Override
    public String getSql() {
        return LIMIT +
                this.limitCondition.getOffset() +
                SEPARATOR +
                this.limitCondition.getLimit();
    }
}
