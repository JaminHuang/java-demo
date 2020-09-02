package com.demo.ibatx.mapper;

import com.demo.ibatx.core.entity.ParamConstant;
import com.demo.ibatx.core.mapper.BaseMapper;
import com.demo.ibatx.provider.InsertBatchProvider;
import com.demo.ibatx.provider.InsertOneSelectiveProvider;
import com.demo.ibatx.provider.anno.InsertSqlProvider;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface InsertMapper<T> extends BaseMapper<T> {

    @InsertSqlProvider(provider = InsertOneSelectiveProvider.class)
    int savex(T t);



    @InsertSqlProvider(provider = InsertBatchProvider.class)
    int saveBatchx(@Param(ParamConstant.ENTITY_LIST) List<T> t);


    //    @InsertSqlProvider(provider = InsertOneProvider.class)
//    int savex(T t);
}
