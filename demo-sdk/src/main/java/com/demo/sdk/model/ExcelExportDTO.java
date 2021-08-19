package com.demo.sdk.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * excel导出
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ExcelExportDTO implements Serializable {

    public final static short floatStyle = 4;

    public final static short intStyle = 3;

    public final static short percentStyle = 0xa;

    public final static short defaultStyle = 0;

    /**
     * @Fields field:field:
     */
    private final static long serialVersionUID = 3167132296393063148L;

    /**
     * 导出的excel文件名称
     */
    private String excelName;

    /**
     * 标题
     */
    private List<String> titles;

    /**
     * 数据集合 其中的每一个元素是Object集合 每个Object按照顺序对应title
     */
    private List<List<Object>> dataList;

    /**
     * 对应HSSFDataFormat.getBuiltinFormat返回的结果 组成的集合 如果使用默认格式 不要设置这个属性 如果有自定义格式
     * 请补齐这个字段 让长度对应titles的长度 每一位分别对应 默认的格式 使用0
     */
    private List<Short> styles;

}
