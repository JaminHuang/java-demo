package com.demo.ibatx.core.entity;

import java.io.Serializable;

/**
 * 排序字段
 */
public class OrderBy implements Serializable {

    private static final long serialVersionUID = -5688082490166136317L;
    private String field;

    private boolean isDesc;

    public OrderBy() {
    }

    public OrderBy(String field) {
        this.field = field;
    }

    public void desc() {
        this.isDesc = true;
    }

    public String getField() {
        return field;
    }

    public boolean isDesc() {
        return isDesc;
    }

    @Override
    public String toString() {
        return "OrderBy(" +
                "field=" + field +
                ", isDesc=" + isDesc +
                ')';
    }

}
