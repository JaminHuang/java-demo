package com.demo.ibatx.generator.template;

import com.demo.ibatx.generator.constant.LoggerFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;

public class ServiceTtlGenerator {

    private Configuration configuration;

    private ServiceFtlParams serviceFtlParams;

    public ServiceTtlGenerator(Configuration configuration, ServiceFtlParams serviceFtlParams) {
        this.configuration = configuration;
        this.serviceFtlParams = serviceFtlParams;
    }

    public void generator() {
        // step1 创建freeMarker配置实例
        Writer interfaceOut = null;
        Writer implOut = null;
        try {
            // step4 加载模版文件
            Template template = configuration.getTemplate("service.ftl");
            // step4 加载模版文件
            Template implTemplate = configuration.getTemplate("serviceImpl.ftl");
            // step5 生成数据
            File interfaceFileDir = new File(serviceFtlParams.getInterfaceFilePath());
            if (!interfaceFileDir.exists()) {
                interfaceFileDir.mkdirs();
            }

            File implFileDir = new File(serviceFtlParams.getImplFilePath());
            if (!implFileDir.exists()) {
                implFileDir.mkdirs();
            }

            File interfaceFile = new File(serviceFtlParams.getInterfaceFilePath() + serviceFtlParams.getClassName() + "Service.java");
            File implFile = new File(serviceFtlParams.getImplFilePath() + serviceFtlParams.getClassName() + "ServiceImpl.java");
            if (!interfaceFile.exists()) {
                if (!interfaceFile.createNewFile()) {
                    throw new RuntimeException("创建文件失败: " + interfaceFile.getName());
                }
            }
            if (!implFile.exists()) {
                if (!implFile.createNewFile()) {
                    interfaceFile.delete();
                    throw new RuntimeException("创建文件失败: " + implFile.getName());
                }
            }
            interfaceOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(interfaceFile)));
            implOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(implFile)));

            // step6 输出文件
            template.process(serviceFtlParams, interfaceOut);
            implTemplate.process(serviceFtlParams, implOut);
            LoggerFactory.getLogger().print("生成service:" + interfaceFile.getName());
            LoggerFactory.getLogger().print("生成service:" + implFile.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != interfaceOut) {
                    interfaceOut.flush();
                }
                if (null != implOut) {
                    implOut.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
