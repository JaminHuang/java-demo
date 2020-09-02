package com.demo.ibatx.provider;

import com.demo.ibatx.core.entity.ParamConstant;
import com.demo.ibatx.sql.builder.SqlBuilder;
import org.apache.ibatis.binding.MapperMethod;

import java.util.List;

/**
 * 批量插入
 */
public class InsertBatchProvider extends BaseProvider {

    @Override
    public String produce(ParamProviderContext paramProviderContext) {
        List list = getInsertList(paramProviderContext.getParameters());
        return SqlBuilder.insert(paramProviderContext.getEntityInfo())
                .entity(list)
                .isSelective(false)
                .getSql();
    }

    private List getInsertList(Object params) {
        if (params instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) params;
            Object entity = paramMap.get(ParamConstant.ENTITY_LIST);
            List list = entity instanceof List ? (List) entity : null;
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    paramMap.put(String.valueOf(i), list.get(i));
                }
            }
            return list;
        }
        if (params instanceof List){
            return  (List) params;
        }
        return null;
    }

}
