package com.demo.ibatx.mapper;

import com.demo.ibatx.core.entity.Condition;
import com.demo.ibatx.core.entity.LimitCondition;
import com.demo.ibatx.core.page.PageInfo;
import com.demo.ibatx.exception.ParamErrorException;

import java.util.Collections;

/**
 * 分页查询
 */
public interface PageSelectMapper<T> extends SelectMapper<T> {

    default PageInfo<T> pageByEntity(PageInfo<T> pageInfo, T t) {
        if (pageInfo.getPageSize() <= 0) {
            throw new ParamErrorException("每页数量不能少于0");
        }
        int count = countx(t);
        pageInfo.setCount(count);
        if (pageInfo.getPageNum() > pageInfo.getTotalPage()) {
            //当前页数，大于总页数
            pageInfo.setList(Collections.emptyList());
            return pageInfo;
        }
        int offset = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
        pageInfo.setList(listLimitx(t, new LimitCondition(offset, pageInfo.getPageSize())));
        return pageInfo;
    }

    default PageInfo<T> pageByCondition(PageInfo<T> pageInfo, Condition<T> condition) {
        if (pageInfo.getPageSize() <= 0) {
            throw new ParamErrorException("每页数量不能少于0");
        }
        int count = countByConditionx(condition);
        pageInfo.setCount(count);
        if (pageInfo.getPageNum() > pageInfo.getTotalPage()) {
            //当前页数，大于总页数
            pageInfo.setList(Collections.emptyList());
            return pageInfo;
        }
        condition.limit(pageInfo.getPageNum(), pageInfo.getPageSize());
        pageInfo.setList(listByConditionx(condition));
        return pageInfo;
    }
}
