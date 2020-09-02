package com.demo.ibatx.sql.builder;

import com.demo.ibatx.core.entity.Condition;
import com.demo.ibatx.core.entity.ParamConstant;
import com.demo.ibatx.exception.ColumnUnknowException;
import com.demo.ibatx.exception.ParamErrorException;
import com.demo.ibatx.helper.EntityHelper;
import com.demo.ibatx.meta.EntityField;
import com.demo.ibatx.meta.EntityInfo;
import com.demo.ibatx.meta.PrimaryKeyField;
import com.demo.ibatx.meta.VersionField;
import com.demo.ibatx.sql.builder.condition.ConditionSqlBuilder;
import com.demo.ibatx.sql.builder.condition.PrimaryKeySqlBuilder;
import com.demo.ibatx.sql.builder.condition.WhereSqlBuilder;

import java.util.*;

/**
 * 更新sql创建
 */
public class UpdateSqlBuilder extends SqlBuilder {

    private Set<String> fieldsNames;

    private Set<String> excludeFieldsNames;

    private boolean isSelective;

    private Object entity;

    UpdateSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public UpdateSqlBuilder excludeFields(Set<String> excludeFieldsNames) {
        this.excludeFieldsNames = excludeFieldsNames;
        return this;
    }

    public UpdateSqlBuilder fields(Set<String> fieldsNames) {
        this.fieldsNames = fieldsNames;
        return this;
    }

    public UpdateSqlBuilder isSelective(boolean isSelective) {
        this.isSelective = isSelective;
        return this;
    }

    public UpdateSqlBuilder where(Condition condition) {
        ConditionSqlBuilder conditionSqlBuilder = new ConditionSqlBuilder(getEntityInfo(), condition);
        if (!condition.isHasCondition()) {
            throw new ParamErrorException("condition 没有有效参数");
        }
        this.setWhereSqlBuilder(conditionSqlBuilder);
        return this;
    }

    public UpdateSqlBuilder wherePk() {
        PrimaryKeyField pk = getEntityInfo().getPrimaryKey();
        if (Objects.isNull(pk)) {
            throw new ColumnUnknowException("entity 没有定义主键。");
        }

        PrimaryKeySqlBuilder primaryKeySqlBuilder = new PrimaryKeySqlBuilder(getEntityInfo(), ParamConstant.ENTITY + DOT + pk.getFieldName());
        this.setWhereSqlBuilder(primaryKeySqlBuilder);
        return this;
    }

    public UpdateSqlBuilder entity(Object entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public String getSql() {
        EntityInfo entityInfo = this.getEntityInfo();
        StringBuilder sql = new StringBuilder(UPDATE);
        sql.append(entityInfo.getTableName());
        sql.append(SET);
        List<EntityField> fieldList = getSelectiveUpdateColumns();
        if (isEmpty(fieldList)) {
            throw new ColumnUnknowException("需要修改的字段为空");
        }
        for (int i = 0; i < fieldList.size(); i++) {
            if (i != 0) {
                sql.append(SEPARATOR);
            }
            EntityField field = fieldList.get(i);
            sql.append(wrapColumn(field.getColumnName()))
                    .append(EQUAL)
                    .append(String.format(REPLACE_PLACE, ParamConstant.ENTITY + DOT + field.getFieldName()));

        }
        VersionField versionField = getEntityInfo().getVersionField();
        if (Objects.nonNull(versionField)) {
            sql.append(SEPARATOR)
                    .append(wrapColumn(versionField.getColumnName()))
                    .append(EQUAL)
                    .append(wrapColumn(versionField.getColumnName()))
                    .append(" +1 ");
        }
        WhereSqlBuilder whereSqlBuilder = this.getWhereSqlBuilder();
        sql.append(whereSqlBuilder.getSql());
        if (Objects.nonNull(versionField) && whereSqlBuilder instanceof PrimaryKeySqlBuilder) {
            EntityHelper entityHelper = new EntityHelper(getEntityInfo(), this.entity);
            try {
                if (Objects.nonNull(entityHelper.getFieldValue(versionField))) {
                    if (whereSqlBuilder.isHasCondition()) {
                        sql.append(AND);
                    }
                    sql.append(versionField.getColumnName())
                            .append(EQUAL)
                            .append(String.format(REPLACE_PLACE, ParamConstant.ENTITY + DOT + versionField.getFieldName()));
                }
            } catch (IllegalAccessException e) {
                throw new ParamErrorException("获取entity的：" + versionField.getFieldName() + "失败");
            }
        }
        return sql.toString();
    }

    private List<EntityField> getSelectiveUpdateColumns() {
        EntityHelper entityHelper = new EntityHelper(getEntityInfo(), this.entity);
        List<EntityField> fieldList;
        if (isNotEmpty(fieldsNames)) {
            fieldList = getSelectiveField(fieldsNames);
        } else if (isNotEmpty(excludeFieldsNames)) {
            fieldList = getSelectiveFieldExclude(excludeFieldsNames);
        } else {
            fieldList = new ArrayList<>(getEntityInfo().getEntityFields());
        }
        if (isSelective) {
            //需要择出不为空的
            try {
                Iterator<EntityField> itr = fieldList.iterator();
                while (itr.hasNext()) {
                    EntityField field = itr.next();
                    Object obj = entityHelper.getFieldValue(field);
                    if (Objects.isNull(obj)) {
                        itr.remove();
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error("获取成员属性对象失败", e);
            }
        }
        //去除不可修复的字段
        fieldList.removeIf(field -> !field.isUpdatable());
        return fieldList;
    }

}
