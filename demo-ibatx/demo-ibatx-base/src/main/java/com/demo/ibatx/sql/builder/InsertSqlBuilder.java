package com.demo.ibatx.sql.builder;

import com.demo.ibatx.exception.ParamErrorException;
import com.demo.ibatx.helper.EntityHelper;
import com.demo.ibatx.meta.EntityField;
import com.demo.ibatx.meta.EntityInfo;
import com.demo.ibatx.meta.LogicDeleteField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 新增sql创建
 */
public class InsertSqlBuilder extends SqlBuilder {

    private boolean isSelective;

    private Object entity;

    protected InsertSqlBuilder(EntityInfo entityInfo) {
        super(entityInfo);
    }

    public InsertSqlBuilder isSelective(boolean isSelective) {
        this.isSelective = isSelective;
        return this;
    }

    public InsertSqlBuilder entity(Object entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public String getSql() {
        StringBuilder sql = new StringBuilder();
        List<EntityField> insertColumns = getSelectiveInsertColumns();
        if (isEmpty(insertColumns)) {
            throw new ParamErrorException("没有需要插入的字段");
        }
        sql.append(INSERT)
                .append(this.getEntityInfo().getTableName())
                .append(getColumnsSql(insertColumns))
                .append(VALUES)
                .append(getInsertValuesSql(insertColumns));
        return sql.toString();
    }

    private String getInsertValuesSql(List<EntityField> insertColumns) {
        StringBuilder insertValuesSql = new StringBuilder();
        if (this.entity instanceof Collection) {
            Collection values = (Collection) this.entity;

            for (int i = 0; i < values.size(); i++) {
                if (i != 0) {
                    insertValuesSql.append(SEPARATOR);
                }
                insertValuesSql.append(getItemValuesSql(insertColumns, i));
            }
        } else {
            insertValuesSql.append(getSingleValuesSql(insertColumns));

        }
        return insertValuesSql.toString();
    }

    private String getSingleValuesSql(List<EntityField> insertFields) {

        StringBuilder insertValueSql = new StringBuilder(OPEN_PLACE);
        for (int i = 0; i < insertFields.size(); i++) {
            if (i != 0) {
                insertValueSql.append(SEPARATOR);
            }
            EntityField entityField = insertFields.get(i);
            if (entityField instanceof LogicDeleteField) {
                insertValueSql.append(((LogicDeleteField) entityField).getIsNotDelete());
            } else {
                insertValueSql.append(String.format(REPLACE_PLACE, entityField.getFieldName()));
            }
        }
        return insertValueSql.append(CLOSED_PLACE).toString();

    }

    private String getItemValuesSql(List<EntityField> insertFields, int index) {

        StringBuilder insertValueSql = new StringBuilder(OPEN_PLACE);
        for (int i = 0; i < insertFields.size(); i++) {
            if (i != 0) {
                insertValueSql.append(SEPARATOR);
            }
            EntityField entityField = insertFields.get(i);
            if (entityField instanceof LogicDeleteField) {
                insertValueSql.append(((LogicDeleteField) entityField).getIsNotDelete());
            } else {
                insertValueSql.append(String.format(REPLACE_PLACE,
                        index + DOT + entityField.getFieldName()));
            }

        }
        return insertValueSql.append(CLOSED_PLACE).toString();

    }

    private String getColumnsSql(List<EntityField> insertColumns) {
        StringBuilder insertColumnsSql = new StringBuilder(OPEN_PLACE);
        for (int i = 0; i < insertColumns.size(); i++) {
            if (i != 0) {
                insertColumnsSql.append(SEPARATOR);
            }
            insertColumnsSql.append(wrapColumn(insertColumns.get(i).getColumnName()));
        }
        insertColumnsSql.append(CLOSED_PLACE);
        return insertColumnsSql.toString();
    }

    public List<EntityField> getSelectiveInsertColumns() {
        List<EntityField> fieldList;
        if (isSelective && !(this.entity instanceof Collection)) {
            EntityHelper entityHelper = new EntityHelper(getEntityInfo(), this.entity);
            fieldList = entityHelper.listNotNullFieldList();
        } else {
            fieldList = new ArrayList<>(getEntityInfo().getEntityFields());
        }
        //去除不可新增的字段
        fieldList.removeIf(entityField -> !entityField.isInsertable());
        return fieldList;
    }
}
