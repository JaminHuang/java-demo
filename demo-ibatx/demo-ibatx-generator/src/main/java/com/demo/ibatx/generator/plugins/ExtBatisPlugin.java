package com.demo.ibatx.generator.plugins;

import com.demo.ibatx.core.annotation.NameStyle;
import com.demo.ibatx.generator.constant.CommonConstant;
import com.demo.ibatx.generator.constant.LoggerFactory;
import com.demo.ibatx.generator.constant.PropertiesKeys;
import com.demo.ibatx.generator.template.TableConfig;
import com.demo.ibatx.generator.template.TemplateGenerator;
import com.demo.ibatx.helper.ColumnNameParserFactory;
import com.demo.ibatx.helper.NameParser;
import com.demo.ibatx.mapper.Mapper;
import com.demo.ibatx.mapper.SoftDeleteMapper;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 自定义生成器
 */
public class ExtBatisPlugin extends DisableDefaultMethodPlugins {

    private Set<String> mappers = new HashSet<>();

    private boolean caseSensitive = true;

    private CommentGeneratorConfiguration commentCfg;

    private NameParser nameParser;

    private TemplateGenerator templateGenerator;

    private boolean isLogicDelete;

    /**
     * 注释生成器
     */

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generatorEntity(topLevelClass, introspectedTable);
        return true;
    }

    private void generatorEntity(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //添加domain的import
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.ToString");

        //添加domain的注解
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@ToString");
        topLevelClass.addImportedType("com.db.ibatx.core.annotation.*");

        //序列化
        FullyQualifiedJavaType serializableType = new FullyQualifiedJavaType(Serializable.class.getName());
        topLevelClass.addImportedType(serializableType);
        topLevelClass.addSuperInterface(serializableType);
        Field serialField = new Field();
        serialField.setVisibility(JavaVisibility.PRIVATE);
        serialField.setStatic(true);
        serialField.setFinal(true);
        serialField.setName("serialVersionUID");
        serialField.setType(new FullyQualifiedJavaType(long.class.getName()));
        serialField.setInitializationString(generatorSeriableUID(topLevelClass.getType()) + "L");
        topLevelClass.getFields().add(0,serialField);

        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        boolean pageable = CommonConstant.TRUE.equalsIgnoreCase(tableConfiguration.getProperty("pageable"));


        if (pageable) {
            FullyQualifiedJavaType pageType = new FullyQualifiedJavaType("com.demo.sdk.page.Page");
            topLevelClass.addImportedType(pageType);
            topLevelClass.setSuperClass(pageType.getShortName());
        }

        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        //如果包含空格，或者需要分隔符，需要完善
        if (StringUtility.stringContainsSpace(tableName)) {
            tableName = context.getBeginningDelimiter()
                    + tableName
                    + context.getEndingDelimiter();
        }

        //是否忽略大小写，对于区分大小写的数据库，会有用
        if (caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        }

        List<Field> fields = topLevelClass.getFields();

        String idColumn = tableConfiguration.getGeneratedKey().getColumn();
        String versionColumn = tableConfiguration.getProperty("version");
        String logicDeleteColumn = tableConfiguration.getProperty("logicDelete");
        for (Field field : fields) {
            String column = nameParser.parse(field.getName());
            if (column.equals(idColumn)) {
                field.addAnnotation("@Id()");
            }
            if (column.equals(versionColumn)) {
                field.addAnnotation("@Version()");
            }
            if (column.equals(logicDeleteColumn)) {
                field.addAnnotation("@LogicDelete()");
                this.isLogicDelete = true;
            }
        }

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            Field field = new Field();
            field.setVisibility(JavaVisibility.PUBLIC);
            field.setStatic(true);
            field.setFinal(true);
            field.setName(introspectedColumn.getActualColumnName().toUpperCase());
            field.setType(new FullyQualifiedJavaType(String.class.getName()));
            field.setInitializationString("\"" + introspectedColumn.getJavaProperty() + "\"");
            context.getCommentGenerator().addClassComment(topLevelClass, introspectedTable);
            topLevelClass.addField(field);
        }
        if (CommonConstant.TRUE.equalsIgnoreCase(context.getProperty(PropertiesKeys.SERVICE_DISABLE))) {
            TableConfig tableConfig = new TableConfig();
            tableConfig.setPageable(pageable);
            tableConfig.setLogicDelete(isLogicDelete);
            templateGenerator.generatorService(topLevelClass, introspectedTable, tableConfig);
        }
        LoggerFactory.getLogger().print("生成entity:" + topLevelClass.getType());
    }

    private String generatorSeriableUID(FullyQualifiedJavaType type) {
        long packageNameHash = type.getPackageName().hashCode();
        long classNameHash = type.getShortName().hashCode();
        return String.valueOf(packageNameHash << 31 + classNameHash);
    }

    public String getDelimiterName(String name) {
        return name;
    }

    /**
     * 生成的Mapper接口
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        //import接口
        for (String mapper : mappers) {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
        }
        if (isLogicDelete) {
            interfaze.addImportedType(new FullyQualifiedJavaType(SoftDeleteMapper.class.getName()));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(SoftDeleteMapper.class.getName() + "<" + entityType.getShortName() + ">"));
        }
        //import实体类
        interfaze.addImportedType(entityType);
        interfaze.addJavaDocLine("/**");
        if (StringUtility.stringHasValue(introspectedTable.getRemarks())) {
            interfaze.addJavaDocLine(" * " + introspectedTable.getRemarks() + "Mapper");
        }
        interfaze.addJavaDocLine(" * ");
        interfaze.addJavaDocLine(" * @author " + getContext().getProperty(PropertiesKeys.AUTHOR));
        interfaze.addJavaDocLine(" * @date " + LocalDateTime.now());
        interfaze.addJavaDocLine(" */");
        LoggerFactory.getLogger().print("生成mapper:" + interfaze.getType());
        return true;
    }

    /**
     * 生成实体类注解KEY对象
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generatorEntity(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 生成带BLOB字段的对象
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generatorEntity(topLevelClass, introspectedTable);
        return false;
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        try {
            templateGenerator = new TemplateGenerator(context);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        //设置默认的注释生成器
        commentCfg = new CommentGeneratorConfiguration();
        commentCfg.setConfigurationType(ExtCommentGenerator.class.getCanonicalName());
        getContext().getProperties().forEach((k, v) -> commentCfg.addProperty(k.toString(), v.toString()));
        context.setCommentGeneratorConfiguration(commentCfg);
        //支持oracle获取注释#114
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
        //支持mysql获取注释
        context.getJdbcConnectionConfiguration().addProperty("useInformationSchema", "true");
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType) {
        //不生成getter
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType) {
        //不生成setter
        return false;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        mappers.add(Mapper.class.getName());
        caseSensitive = true;
        this.nameParser = ColumnNameParserFactory.getParser(NameStyle.camelhumpAndLowercase);
    }
}
