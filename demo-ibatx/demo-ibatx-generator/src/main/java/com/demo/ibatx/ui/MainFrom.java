package com.demo.ibatx.ui;

import com.alibaba.fastjson.JSONObject;
import com.demo.ibatx.generator.Generator;
import com.demo.ibatx.generator.constant.LoggerFactory;
import com.demo.ibatx.generator.constant.PropertiesKeys;
import com.demo.ibatx.generator.template.TableDetail;
import com.demo.ibatx.ui.module.*;
import com.demo.ibatx.util.FileUtils;
import com.demo.ibatx.util.OsUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

public class MainFrom extends JPanel {

    public MainFrom(Configuration configuration) {

        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        ArrayList<ModuleInitializer> moduleInitializerList = new ArrayList<>();

        ProjectModule projectModule = new ProjectModule(configuration);
        moduleInitializerList.add(projectModule);
        this.add(new ModuleJPanel("项目配置", projectModule), new Constraints(0, 0, 3, 1).setIpad(10, 10));

        TableModule tableModule = new TableModule(configuration);
        moduleInitializerList.add(tableModule);
        this.add(new ModuleJPanel("表配置", tableModule), new Constraints(0, 3, 3, 1).setIpad(10, 10));

        OperatorModule operatorModule = new OperatorModule(configuration);
        moduleInitializerList.add(operatorModule);
        this.add(new ModuleJPanel("操作", operatorModule), new Constraints(0, 4, 3, 1).setIpad(10, 10));

        LoggerModule loggerModule = new LoggerModule(configuration);
        moduleInitializerList.add(loggerModule);
        this.add(new ModuleJPanel("日志", loggerModule), new Constraints(0, 5, 3, 2).setIpad(10, 10));

        moduleInitializerList.forEach(ModuleInitializer::initialize);
        File baseDir = new File(configuration.getBaseDir());
        configuration.getLogger().print("当前工作文件目录：" + baseDir.getAbsolutePath());
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (!arg.startsWith("-P")) {
                    continue;
                }
                arg = arg.replace("-P", "");
                if (arg.contains("=")) {
                    String[] kv = arg.split("=");
                    params.put(kv[0], kv[1]);
                } else {
                    params.put(arg, null);
                }
            }
        }
        String baseDirProperty;
        if (params.containsKey(PropertiesKeys.BASE_FILE)) {
            baseDirProperty = params.get(PropertiesKeys.BASE_FILE);
        } else {
            baseDirProperty = "." + OsUtil.getFileSep();
        }
        if (params.containsKey(PropertiesKeys.BACKGROUND)) {
            generator(params);
        } else {
            JFrame frame = new JFrame("mybatis 代码生成器");

            Configuration configuration = new Configuration();
            LoggerFactory.setLogger(configuration.getLogger());

            configuration.setBaseDir(baseDirProperty);
            configuration.setJFrame(frame);
            JPanel mainPanel = new MainFrom(configuration);
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
    }

    private static void generator(Map<String,String> params) throws Exception {
        String baseDirProperty;
        if (params.containsKey(PropertiesKeys.BASE_FILE)) {
            baseDirProperty = params.get(PropertiesKeys.BASE_FILE);
        } else {
            baseDirProperty = "." + OsUtil.getFileSep();
        }
        String project = params.get(PropertiesKeys.PROJECT_NAME);
        if (Objects.isNull(project)) {
            LoggerFactory.getLogger().print("没有选择项目");
            throw new RuntimeException("没有选择项目");
        }
        String tableJson = FileUtils.readFile(new File(baseDirProperty+"/table/tableList.json"));
        List<TableDetail> tableDetails = JSONObject.parseArray(tableJson, TableDetail.class);
        new Generator().generator(baseDirProperty, project + ".properties", tableDetails);
    }

}
