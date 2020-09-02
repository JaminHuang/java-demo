package com.demo.ibatx.sql.builder.condition;

import com.demo.ibatx.core.entity.ParamConstant;
import com.demo.ibatx.core.entity.ParamSymbol;
import com.demo.ibatx.exception.ColumnUnknowException;
import com.demo.ibatx.meta.EntityInfo;
import com.demo.ibatx.meta.ParamValue;
import com.demo.ibatx.meta.PrimaryKeyField;

import java.util.Objects;

public class PrimaryKeySqlBuilder extends WhereSqlBuilder {

    private String replacePlace;

    public PrimaryKeySqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public PrimaryKeySqlBuilder(EntityInfo entityInfo, String replacePlace) {
        super(entityInfo);
        this.replacePlace = replacePlace;
    }

    @Override
    protected StringBuilder getWhereSql() {
        StringBuilder stringBuilder = new StringBuilder();
        replacePlace = hasLength(replacePlace) ? replacePlace : ParamConstant.PRIMARY_KEY;
        PrimaryKeyField primaryKeyField = getEntityInfo().getPrimaryKey();
        if (Objects.isNull(primaryKeyField)) {
            throw new ColumnUnknowException("entity 没有定义主键。");
        }
        stringBuilder.append(getParam(new ParamValue(primaryKeyField.getColumnName(), ParamSymbol.EQUAL, replacePlace)));
        this.setHasCondition(true);

        return stringBuilder;
    }
}
