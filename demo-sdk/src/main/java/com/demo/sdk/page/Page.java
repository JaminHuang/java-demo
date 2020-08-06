package com.demo.sdk.page;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 页码类
 */
public class Page {

    /**
     * 当前页
     */
    private Integer num = 1;

    /**
     * 每页数
     */
    private Integer row = 10;

    /**
     * 分页起始位置
     */
    private Integer start;

    /**
     * 排序字段
     */
    private String sort;

    /**
     * 排序类型 对于比较复杂的排序规则 根据传入的此字段 使用不同的排序规则
     */
    private Integer sortType;

    /**
     * 接口的版本号
     */
    private Integer version = 1;
    /**
     * 最大行数
     */
    @JsonIgnore
    private static final Integer MAX_ROW = 2000;

    public Page() {
        super();
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getRow() {
        if(row == null){
            return row;
        }
        // 分页最大2000条，超出按最大值2000处理
        return row >= MAX_ROW ? MAX_ROW : row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getStart() {
        if (num != null && row != null) {
            start = (num - 1) * row;
        }
        return start;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public static Integer getMaxRow() {
        return MAX_ROW;
    }
}
