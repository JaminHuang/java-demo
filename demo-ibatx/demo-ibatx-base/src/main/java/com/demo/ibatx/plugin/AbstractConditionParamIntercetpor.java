package com.demo.ibatx.plugin;

import com.demo.ibatx.core.entity.Condition;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Map;
import java.util.Objects;

/**
 * 对condition参数的拦截器
 */
public abstract class AbstractConditionParamIntercetpor implements InterceptorAdapter {

    @Override
    public boolean intercept(MappedStatement mappedStatement, Map<String, Object> mapperMethodParam) {
        if (Objects.isNull(mapperMethodParam) || mapperMethodParam.isEmpty()) {
            return true;
        }
        Condition<?> condition = null;
        for (Object c : mapperMethodParam.values()) {
            if (c instanceof Condition) {
                condition = (Condition) c;
                break;
            }
        }
        if (Objects.isNull(condition)) {
            return true;
        }
        return this.interceptCondition(mappedStatement, condition);
    }

    /**
     * 拦截 Condition 条件
     *
     * @param mappedStatement mapper语句
     * @param condition       条件
     * @return 是否截断 true - 不拦截 ; false - 拦截
     * @author ligt
     * @date 2019/11/28  5:41 PM
     */
    public abstract boolean interceptCondition(MappedStatement mappedStatement, Condition<?> condition);
}
