package ${serviceImplPackage};

import com.demo.ibatx.core.entity.Condition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
<#if pageable>
import com.demo.ibatx.core.entity.LimitCondition;
import com.demo.sdk.page.Page;
</#if>
import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.StringUtils;
import com.demo.sdk.util.CollectionUtils;
import com.demo.sdk.util.Assert;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import ${entityPackage}.${className};
import ${clientPackage}.${className}Mapper;
import ${serviceInterfacePackage}.${className}Service;

/**
 * ${tableRemark}Service实现类
 *
 * @author ${author}
 * @date ${date}
 */
@Service
public class ${className}ServiceImpl implements ${className}Service {

    @Autowired
    private ${className}Mapper ${entityClassName}Mapper;

    @Override
    public ${className} get(Integer id) {
        return ${entityClassName}Mapper.getx(id);
    }

    @Override
    public ${className} getOne(${className} ${entityClassName}) {
        Assert.notNull(${entityClassName}, "${entityClassName}参数不能为空");
        return ${entityClassName}Mapper.getOnex(${entityClassName});
    }

    @Override
    public int save(${className} ${entityClassName}) {
        Assert.notNull(${entityClassName}, "${entityClassName}参数不能为空");
        if (StringUtils.isEmpty(${entityClassName}.getCreator())) {
            ${entityClassName}.setCreator(ReqThreadLocal.getUserName());
        }
        if (StringUtils.isEmpty(${entityClassName}.getUpdater())) {
            ${entityClassName}.setUpdater(ReqThreadLocal.getUserName());
        }
        return ${entityClassName}Mapper.savex(${entityClassName});
    }

    @Override
    @Transactional
    public ${className} saveAndGet(${className} ${entityClassName}) {
        Assert.notNull(${entityClassName}, "${entityClassName}参数不能为空");
        if (StringUtils.isEmpty(${entityClassName}.getCreator())) {
            ${entityClassName}.setCreator(ReqThreadLocal.getUserName());
        }
        if (StringUtils.isEmpty(${entityClassName}.getUpdater())) {
            ${entityClassName}.setUpdater(ReqThreadLocal.getUserName());
        }
        ${entityClassName}Mapper.savex(${entityClassName});
        if (Objects.nonNull(${entityClassName}.getId())) {
            return this.get(${entityClassName}.getId());
        }
        return ${entityClassName};
    }

    @Override
    public int update(${className} ${entityClassName}) {
        Assert.notNull(${entityClassName}, "${entityClassName}参数不能为空");
        if (StringUtils.isEmpty(${entityClassName}.getUpdater())) {
            ${entityClassName}.setUpdater(ReqThreadLocal.getUserName());
        }
        return ${entityClassName}Mapper.updatex(${entityClassName});
    }

    @Override
    public List<${className}> listByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        Condition<${className}> condition = new Condition<>();
        condition.createCriteria().andIn(${className}.ID, ids);
        condition.limit(Page.getMaxRow());
        return ${entityClassName}Mapper.listByConditionx(condition);
    }

    @Override
    public List<${className}> list(${className} ${entityClassName}) {
        Assert.notNull(${entityClassName}, "${entityClassName}参数不能为空");
        <#if pageable>
        return ${entityClassName}Mapper.listLimitx(${entityClassName}, new LimitCondition(${entityClassName}.getStart(), ${entityClassName}.getRow()));
        <#else >
        return ${entityClassName}Mapper.listx(${entityClassName});
        </#if>
    }

    @Override
    public int count(${className} ${entityClassName}) {
        Assert.notNull(${entityClassName}, "${entityClassName}参数不能为空");
        return ${entityClassName}Mapper.countx(${entityClassName});
    }

    @Override
    public Map<Integer, ${className}> mapByIds(List<Integer> ids) {
        List<${className}> ${entityClassName}List  = this.listByIds(ids);
        return ${entityClassName}List.stream().collect(Collectors.toMap(${className}::getId, Function.identity()));
    }

    @Override
    public List<${className}> listByCondition(Condition<${className}> condition) {
        Assert.notNull(condition, "condition参数不能为空");
        return ${entityClassName}Mapper.listByConditionx(condition);
    }

    @Override
    public int countByCondition(Condition<${className}> condition) {
        Assert.notNull(condition, "condition参数不能为空");
        return ${entityClassName}Mapper.countByConditionx(condition);
    }

    @Override
    public int realDelete(Integer id) {
        return ${entityClassName}Mapper.realDeletex(id);
    }

<#if logicDelete>
    @Override
    public int softDelete(Integer id){
        return ${entityClassName}Mapper.softDeletex(id);
    }
</#if>
}
