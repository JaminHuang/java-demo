package com.demo.ibatx.sql.builder.condition;

import com.demo.ibatx.core.entity.ParamConstant;
import com.demo.ibatx.core.entity.ParamSymbol;
import com.demo.ibatx.helper.EntityHelper;
import com.demo.ibatx.meta.EntityField;
import com.demo.ibatx.meta.EntityInfo;
import com.demo.ibatx.meta.LogicDeleteField;

import java.util.Objects;

public class EntitySqlBuilder extends WhereSqlBuilder {

    private Object entity;

    EntitySqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public EntitySqlBuilder(EntityInfo entityInfo, Object entity) {
        super(entityInfo);
        this.entity = entity;
    }

    @Override
    protected StringBuilder getWhereSql() {
        StringBuilder conditionSql = new StringBuilder();
        try {
            LogicDeleteField logicDeleteField = getEntityInfo().getLogicDeleteField();
            int index = 0;
            EntityHelper entityHelper = new EntityHelper(getEntityInfo(), this.entity);
            for (EntityField field : getEntityInfo().getEntityFields()) {
                field.getField().setAccessible(true);
                Object obj = entityHelper.getFieldValue(field);
                if (Objects.isNull(obj)) {
                    continue;
                }
                if (!isHasCondition()) {
                    setHasCondition(true);
                }
                //如果查询字段中有逻辑删除字段，则标记是否条件有删除字段为true
                if (Objects.nonNull(logicDeleteField)
                        && Objects.equals(logicDeleteField.getFieldName(), field.getFieldName())) {
                    setHasDeleteCondition(true);
                }

                if (index++ != 0) {
                    conditionSql.append(AND);
                }
                conditionSql.append(wrapColumn(field.getColumnName()))
                        .append(ParamSymbol.EQUAL.getValue())
                        .append(String.format(REPLACE_PLACE, ParamConstant.ENTITY + DOT + field.getFieldName()));
            }
        } catch (IllegalAccessException e) {
            logger.error("获取成员属性对象失败", e);
        }
        return conditionSql;
    }

}
