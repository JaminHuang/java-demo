package com.demo.ibatx.generator.template;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceFtlParams {

    private String serviceInterfacePackage;

    private String interfaceFilePath;

    private String className;

    private String entityClassName;

    private String date;

    private String author;

    private String entityPackage;

    private String clientPackage;

    private String serviceImplPackage;

    private String constsPackage;

    private String implFilePath;

    private String id;

    private String tableRemark;

    private boolean pageable;

    private boolean logicDelete;
}
