package com.demo.ibatx.mapper;

import com.demo.ibatx.core.entity.ParamConstant;
import com.demo.ibatx.core.mapper.BaseMapper;
import com.demo.ibatx.provider.DeleteByPrimaryKeyProvider;
import com.demo.ibatx.provider.anno.DeleteSqlProvider;
import org.apache.ibatis.annotations.Param;

/**
 * 删除mapper
 */
public interface DeleteMapper<T> extends BaseMapper<T> {

    /**
     * 物理删除
     *
     * @param t 主键
     * @return 删除结果
     */
    @DeleteSqlProvider(provider = DeleteByPrimaryKeyProvider.class)
    int realDeletex(@Param(ParamConstant.PRIMARY_KEY) Object t);

}
