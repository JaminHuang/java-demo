package com.demo.ibatx.core.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 查询参数
 */
public class ParamCondition implements Serializable {

    private ParamSymbol paramSymbol;

    private String paramName;

    private String fieldName;

    private Object value;

    private ParamRelEnum paramRel = ParamRelEnum.AND;

    public ParamCondition() {
    }
    public ParamCondition(int criteriaIdxint, int paramIndex, String fieldName, ParamSymbol paramSymbol, Object value) {
        this.paramSymbol = paramSymbol;
        this.paramName = criteriaIdxint + "_" + fieldName + "_" + paramIndex;
        this.fieldName = fieldName;
        this.value = value;
    }

    public ParamCondition(ParamRelEnum paramRel, int criteriaIdxint, int paramIndex, String fieldName, ParamSymbol paramSymbol, Object value) {
        this.paramSymbol = paramSymbol;
        this.paramName = criteriaIdxint + "_" + fieldName + "_" + paramIndex;
        this.fieldName = fieldName;
        this.value = value;
        this.paramRel = paramRel;
    }

    public ParamRelEnum getParamRel() {
        return paramRel;
    }

    public void setParamRel(ParamRelEnum paramRel) {
        this.paramRel = paramRel;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ParamSymbol getParamSymbol() {
        return paramSymbol;
    }

    public void setParamSymbol(ParamSymbol paramSymbol) {
        this.paramSymbol = paramSymbol;
    }

    public String getParamPlaceName() {
        return ParamConstant.CONDITION + "." + ParamConstant.PARAMS + "." + getParamName();
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public String toString() {
        return this.getFieldName() + " " + this.getParamSymbol().value + (Objects.isNull(this.getValue()) ? "" : this.getValue());
    }

}
