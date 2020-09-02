package ${serviceInterfacePackage};

import com.demo.ibatx.core.entity.Condition;
import java.util.List;
import java.util.Map;
import com.demo.rpc.annotation.Remote;
import ${entityPackage}.${className};
import ${constsPackage}.Application;


/**
 * ${tableRemark}Service接口
 *
 * @author ${author}
 * @date ${date}
 */
@Remote(applicationName = Application.name)
public interface ${className}Service {

    /**
     *  根据主键查询
     *
     *  @param   id  主键
     *  @return  ${className}
     *  @author  ${author}
     *  @date    ${date}
     */
   ${className} get(Integer id);

    /**
     *  获取单个
     *
     *  @param   ${entityClassName}  查询条件
     *  @return  ${className}
     *  @author  ${author}
     *  @date    ${date}
     */
    ${className} getOne(${className} ${entityClassName});

    /**
     *  新增数据
     *
     *  @param   ${entityClassName}  新增内容
     *  @return  int
     *  @author  ${author}
     *  @date    ${date}
     */
    int save(${className} ${entityClassName});

    /**
     *  新增数据并取回数据
     *
     *  @param   ${entityClassName}  新增内容
     *  @return  int
     *  @author  ${author}
     *  @date    ${date}
     */
    ${className} saveAndGet(${className} ${entityClassName});

    /**
     *  根据主键更新非空数据
     *
     *  @param   ${entityClassName}  更新内容
     *  @return  int
     *  @author  ${author}
     *  @date    ${date}
     */
    int update(${className} ${entityClassName});

    /**
     *  根据主键Id查询数据列表
     *
     *  @param   ids  主键值
     *  @return  ${className}数组
     *  @author  ${author}
     *  @date    ${date}
     */
    List<${className}> listByIds(List<Integer> ids);

    /**
     *  根据条件查询数据列表
     *
     *  @param   ${entityClassName}  主键值
     *  @return  ${className}数组
     *  @author  ${author}
     *  @date    ${date}
     */
    List<${className}> list(${className} ${entityClassName});

    /**
     *  查询数据数量
     *
     *  @param   ${entityClassName}  查询条件
     *  @return  int 符合条件的数量
     *  @author  ${author}
     *  @date    ${date}
     */
    int count(${className} ${entityClassName});

    /**
     *  将符合查询条件数据列表转map
     *
     *  @param   ids  主键值
     *  @return  map  查询结果主键map
     *  @author  ${author}
     *  @date    ${date}
     */
    Map<Integer, ${className}> mapByIds(List<Integer> ids);

    /**
     *  查询数据列表
     *
     *  @param   condition  查询条件
     *  @return  ${className}数组
     *  @author  ${author}
     *  @date    ${date}
     */
    List<${className}> listByCondition(Condition<${className}> condition);

    /**
     *  查询符合条件的数量
     *
     *  @param   condition  查询条件
     *  @return  int  符合条件的数量
     *  @author  ${author}
     *  @date    ${date}
     */
    int countByCondition(Condition<${className}> condition);

    /**
     *  根据主键物理删除
     *
     *  @param   id   数据主键
     *  @return  int  生效的数据
     *  @author  ${author}
     *  @date    ${date}
     */
    int realDelete(Integer id);
<#if logicDelete>
    /**
     *  根据主键逻辑删除
     *
     *  @param   id   数据主键
     *  @return  int  生效的数据
     *  @author  ${author}
     *  @date    ${date}
     */
    int softDelete(Integer id);
</#if>
}
