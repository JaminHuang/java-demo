package com.demo.ibatx.meta;

import com.demo.ibatx.core.entity.ParamSymbol;

/**
 * 查询参数
 */
public class ParamValue {

    public ParamValue(String columnName, ParamSymbol paramSymbol, String replacePlaceName) {
        this.paramSymbol = paramSymbol;
        this.columnName = columnName;
        this.replacePlaceName = replacePlaceName;
    }

    public ParamValue(String columnName, ParamSymbol paramSymbol, Object value) {
        this.columnName = columnName;
        this.paramSymbol = paramSymbol;
        this.value = value;
    }

    private String columnName;

    private String replacePlaceName;

    private Object value;

    private ParamSymbol paramSymbol;

    public ParamSymbol getParamSymbol() {
        return paramSymbol;
    }

    public void setParamSymbol(ParamSymbol paramSymbol) {
        this.paramSymbol = paramSymbol;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getReplacePlaceName() {
        return replacePlaceName;
    }

    public void setReplacePlaceName(String replacePlaceName) {
        this.replacePlaceName = replacePlaceName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
