package com.demo.ibatx.mapper;

import com.demo.ibatx.core.entity.ParamConstant;
import com.demo.ibatx.core.mapper.BaseMapper;
import com.demo.ibatx.provider.LogicDeleteByPrimaryKeyProvider;
import com.demo.ibatx.provider.anno.DeleteSqlProvider;
import org.apache.ibatis.annotations.Param;

public interface SoftDeleteMapper<T> extends BaseMapper<T> {

    /**
     * 逻辑删除
     *
     * @param t
     * @return
     */
    @DeleteSqlProvider(provider = LogicDeleteByPrimaryKeyProvider.class)
    int softDeletex(@Param(ParamConstant.PRIMARY_KEY) Object t);

}
