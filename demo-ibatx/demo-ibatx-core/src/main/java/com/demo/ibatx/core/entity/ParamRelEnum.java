package com.demo.ibatx.core.entity;

public enum ParamRelEnum {
    /**
     * 条件AND
     */
    AND(" AND "),
    /**
     * 条件OR
     */
    OR("OR ")
    ;
    private String value;

    ParamRelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
