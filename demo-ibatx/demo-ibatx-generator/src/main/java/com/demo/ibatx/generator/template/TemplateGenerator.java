package com.demo.ibatx.generator.template;

import com.demo.ibatx.generator.constant.PropertiesKeys;
import com.demo.ibatx.util.DateUtils;
import com.demo.ibatx.util.OsUtil;
import freemarker.template.Configuration;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.springframework.util.Assert;

import java.io.File;

public class TemplateGenerator {

    private Configuration configuration;

    private Context context;

    public TemplateGenerator(Context context) throws Exception {
        this.context = context;
        this.configuration = new Configuration(Configuration.getVersion());
        configuration.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "ftl");
    }

    public void generatorService(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, TableConfig tableConfig) {

        FullyQualifiedJavaType fullyQualifiedJavaType = topLevelClass.getType();
        String entityClass = fullyQualifiedJavaType.getShortName();
        String serviceImplPackage = context.getProperty(PropertiesKeys.SERVICE_IMPL_PACKAGE);
        String serviceImplFile = context.getProperty(PropertiesKeys.SERVICE_IMPL_JAVA);
        String serviceInterfacePackage = context.getProperty(PropertiesKeys.SERVICE_INTERFACE_PACKAGE);
        String serviceInterfaceFile = context.getProperty(PropertiesKeys.SERVICE_INTERFACE_JAVA);
        serviceImplFile = serviceImplFile +
                OsUtil.getFileSep() +
                serviceImplPackage.replaceAll("\\.", OsUtil.getFileSep()) +
                OsUtil.getFileSep();
        serviceInterfaceFile = serviceInterfaceFile +
                OsUtil.getFileSep() +
                serviceInterfacePackage.replaceAll("\\.", OsUtil.getFileSep()) +
                OsUtil.getFileSep();
        File serviceImplFileDir = new File(serviceImplFile);
        if (!serviceImplFileDir.exists()){
            serviceImplFileDir.mkdirs();
        }
        File serviceInterfaceFileDir = new File(serviceInterfaceFile);
        if (!serviceInterfaceFileDir.exists()){
            serviceInterfaceFileDir.mkdirs();
        }
        ServiceFtlParams serviceFtlParams = ServiceFtlParams.builder().author(this.context.getProperty("author"))
                .className(entityClass)
                .clientPackage(context.getProperty(PropertiesKeys.MAPPER_PACKAGE))
                .entityClassName(entityClass.toLowerCase().substring(0, 1) + entityClass.substring(1))
                .author(context.getProperty(PropertiesKeys.AUTHOR))
                .date(DateUtils.getDateString())
                .entityPackage(context.getProperty(PropertiesKeys.COMMON_MODEL_PACKAGE))
                .serviceImplPackage(serviceImplPackage)
                .implFilePath(serviceImplFile)
                .serviceInterfacePackage(serviceInterfacePackage)
                .interfaceFilePath(serviceInterfaceFile)
                .tableRemark(introspectedTable.getRemarks())
                .pageable(tableConfig.isPageable())
                .logicDelete(tableConfig.isLogicDelete())
                .constsPackage(context.getProperty(PropertiesKeys.COMMON_MODEL_PACKAGE).replace("po", "consts"))
                .build();

        Assert.notNull(serviceFtlParams.getServiceImplPackage(), "service impl的包不能为空");
        Assert.notNull(serviceFtlParams.getServiceInterfacePackage(), "service 接口包不能为空");
        Assert.notNull(serviceFtlParams.getImplFilePath(), "service impl 的项目java 路径不能为空");
        Assert.notNull(serviceFtlParams.getInterfaceFilePath(), "service 接口的项目java 路径不能为空");

        new ServiceTtlGenerator(this.configuration, serviceFtlParams).generator();

    }

}
