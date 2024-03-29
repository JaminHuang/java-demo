package com.demo.ibatx.sql.builder;

import com.demo.ibatx.meta.EntityInfo;
import com.demo.ibatx.sql.builder.condition.WhereSqlBuilder;

public abstract class SqlBuilder extends CommonBuilder{

    private WhereSqlBuilder whereSqlBuilder;

    public WhereSqlBuilder getWhereSqlBuilder() {
        return whereSqlBuilder;
    }

    public void setWhereSqlBuilder(WhereSqlBuilder whereSqlBuilder) {
        this.whereSqlBuilder = whereSqlBuilder;
    }

    protected SqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public static SelectSqlBuilder select(EntityInfo entityInfo) {
        return new SelectSqlBuilder(entityInfo);
    }

    public static UpdateSqlBuilder update(EntityInfo entityInfo) {
        return new UpdateSqlBuilder(entityInfo);
    }

    public static InsertSqlBuilder insert(EntityInfo entityInfo) {
        return new InsertSqlBuilder(entityInfo);
    }


    public static DeleteSqlBuilder delete(EntityInfo entityInfo) {
        return new DeleteSqlBuilder(entityInfo);
    }


}
