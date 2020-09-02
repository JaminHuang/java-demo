package com.demo.ibatx.generator;

import com.demo.ibatx.generator.constant.LoggerFactory;
import com.demo.ibatx.generator.constant.PropertiesKeys;
import com.demo.ibatx.generator.template.TableDetail;
import com.demo.ibatx.util.FileUtils;
import com.demo.ibatx.util.OsUtil;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class Generator {

    /**
     * 2019/02/16 15:58
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/mm/dd HH:mm");

    public static void main(String[] args) throws Exception {
        String baseDirProperty = "";
        if (Objects.nonNull(args) && args.length > 0) {
            baseDirProperty = args[0];
        } else {
            baseDirProperty = "." + OsUtil.getFileSep() + ".." + OsUtil.getFileSep();
        }
        new Generator().generator(baseDirProperty, "ac-core-message.properties", null);
    }

    public void generator(String baseDir, String project, List<TableDetail> tableList) throws Exception {
        File file = new File(baseDir);
        LoggerFactory.getLogger().print(" 配置文件目录 ：" + file.getAbsolutePath());
        if (!file.exists()) {
            throw new RuntimeException("配置文件目录不存在");
        }

        if (StringUtils.isEmpty(project)) {
            throw new RuntimeException("没有选择项目");
        }
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config =
                cp.parseConfiguration(FileUtils.getResourceAsStream("config/generatorConfig.xml"));

        Properties properties = new Properties();

        properties.load(new FileInputStream(baseDir + OsUtil.getFileSep() + "project" + OsUtil.getFileSep() + project));

        config.getContext("Mysql").addProperty(PropertiesKeys.BASE_FILE, baseDir);

        Assert.notNull(properties.getProperty(PropertiesKeys.MAPPER_PACKAGE), "mapper 包不能为空");
        Assert.notNull(properties.getProperty(PropertiesKeys.MAPPER_XML), "mapper xml的路径为空");
        Assert.notNull(properties.getProperty(PropertiesKeys.MAPPER_JAVA), "mapper项目的Java路径不能为空");
        Assert.notNull(properties.getProperty(PropertiesKeys.COMMON_JAVA), "PO项目的Java路径不能为空");
        Assert.notNull(properties.getProperty(PropertiesKeys.COMMON_MODEL_PACKAGE), "PO项目的包不能为空");

        config.getContexts().forEach(context -> {

            properties.forEach((k, v) -> context.addProperty(k.toString(), v.toString()));
            JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = context.getJavaClientGeneratorConfiguration();
            javaClientGeneratorConfiguration.setTargetPackage(properties.getProperty(PropertiesKeys.MAPPER_PACKAGE));
            javaClientGeneratorConfiguration.setTargetProject(properties.getProperty(PropertiesKeys.MAPPER_JAVA));

            SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = context.getSqlMapGeneratorConfiguration();
            sqlMapGeneratorConfiguration.setTargetProject(properties.getProperty(PropertiesKeys.MAPPER_XML));

            JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = context.getJavaModelGeneratorConfiguration();
            javaModelGeneratorConfiguration.setTargetPackage(properties.getProperty(PropertiesKeys.COMMON_MODEL_PACKAGE));
            javaModelGeneratorConfiguration.setTargetProject(properties.getProperty(PropertiesKeys.COMMON_JAVA));
            if (CollectionUtils.isEmpty(tableList)) {
                return;
            }
            context.getTableConfigurations().removeIf(table -> table.getTableName().equals("empty"));
            for (TableDetail table : tableList) {
                TableConfiguration tableConfiguration = new TableConfiguration(context);
                tableConfiguration.setTableName(table.getTableName());
                tableConfiguration.setSchema(table.getSchema());
                tableConfiguration.setDomainObjectName(table.getDomainObjectName());
                tableConfiguration.setGeneratedKey(new GeneratedKey(table.getIdName(), "MYSQL", true, null));
                tableConfiguration.addProperty(TableDetail.ID_NAME, table.getIdName());
                tableConfiguration.addProperty(TableDetail.VERSION_NAME, table.getVersionName());
                tableConfiguration.addProperty(TableDetail.LOGIC_DELETE, table.getLogicDeleteName());
                tableConfiguration.addProperty(TableDetail.PAGEABLE, table.getPageable());
                context.addTableConfiguration(tableConfiguration);
            }
        });
        LoggerFactory.getLogger().print("开始生成.......");
        LoggerFactory.getLogger().print("连接数据库:" + config.getContext("Mysql").getJdbcConnectionConfiguration().getConnectionURL());
        LoggerFactory.getLogger().print("当前项目为:" + project);
        LoggerFactory.getLogger().print("需要生成的表有:"+tableList.stream().map(TableDetail::getTableName).collect(Collectors.joining(",")));
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for (String warning : warnings) {
            LoggerFactory.getLogger().print(warning);
        }
    }


}
