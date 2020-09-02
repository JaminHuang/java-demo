package com.demo.ibatx.ui.module;

import com.demo.ibatx.generator.constant.PropertiesKeys;
import com.demo.ibatx.ui.Configuration;
import com.demo.ibatx.util.FileUtils;
import com.demo.ibatx.util.OsUtil;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static com.demo.ibatx.ui.module.ProjectModule.FILE_FIX;

public class ProjectDetail {

    private ProjectModule projectModule;

    private JTextArea projectConfig;

    private JPanel projectDetail;

    private JButton confirmButton;

    private String projectName;

    private Configuration configuration;

    private boolean isInsert;

    private JFrame frame;

    public ProjectDetail(ProjectModule projectModule, Configuration configuration) {

        this.projectModule = projectModule;
        this.configuration = configuration;
        this.init();
    }

    private void init() {
        this.projectDetail = new JPanel();
        this.projectDetail.setPreferredSize(new Dimension(680, 680));
        this.projectDetail.setLayout(new BorderLayout());

        this.projectConfig = new JTextArea();
        this.projectConfig.setPreferredSize(new Dimension(680, 680));
        this.projectConfig.setLineWrap(true);
        this.projectDetail.add(projectConfig, BorderLayout.CENTER);

        this.confirmButton = new JButton("确认");
        this.projectDetail.add(confirmButton, BorderLayout.SOUTH);

        this.confirmButton.addActionListener((e) -> {
            String projectText = this.projectConfig.getText();
            Properties properties = new Properties();
            try {
                properties.load(new ByteArrayInputStream(projectText.getBytes()));
            } catch (Exception e1) {
                configuration.getLogger().print("保存失败");
                configuration.getLogger().print(e1.getMessage());
                JOptionPane.showMessageDialog(null, "保存失败", "提示", JOptionPane.ERROR_MESSAGE);

                return;
            }
            this.projectName = properties.getProperty(PropertiesKeys.PROJECT_NAME);
            if (!StringUtils.hasLength(projectText)) {
                configuration.getLogger().print("请配置项目名字");
                JOptionPane.showMessageDialog(null, "请配置项目名字", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isInsert) {
                configuration.getLogger().print("新增项目：" + this.projectName);
                if (this.projectModule.containsProject(projectName)) {
                    configuration.getLogger().print("项目：" + this.projectName + "已经存在");
                    JOptionPane.showMessageDialog(null, "项目：" + this.projectName + "已经存在", "提示", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                File file = new File(this.projectModule.getProjectFileDir() + OsUtil.getFileSep() + this.projectName + FILE_FIX);
                try {
                    file.createNewFile();
                    FileUtils.writeFile(file, projectText);
                    this.projectModule.addProject(file);
                } catch (Exception e1) {
                    configuration.getLogger().print("保存项目详情失败");
                    configuration.getLogger().print(e1.getMessage());
                    JOptionPane.showMessageDialog(null, "保存项目详情失败", "提示", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                configuration.getLogger().print("修改项目：" + this.projectName);
                File file = new File(this.projectModule.getProjectFileDir() + OsUtil.getFileSep() + this.projectName + FILE_FIX);
                try {
                    FileUtils.writeFile(file, projectText);
                } catch (Exception e1) {
                    configuration.getLogger().print("保存项目详情失败");
                    configuration.getLogger().print(e1.getMessage());
                    JOptionPane.showMessageDialog(null, "保存项目详情失败", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
            this.frame.dispose();
        });
    }

    public void show(String projectName) {
        this.projectName = projectName;
        this.isInsert = false;
        try {
            File file = new File(projectModule.getProjectFileDir() + OsUtil.getFileSep() + projectName + FILE_FIX);
            this.show(new FileInputStream(file));
        } catch (Exception e) {
            configuration.getLogger().print("打开项目详情失败");
            configuration.getLogger().print(e.getMessage());
        }
    }

    public void show() {
        this.isInsert = true;
        try {
            this.show(projectModule.getTmpFile());
        } catch (Exception e) {
            configuration.getLogger().print("打开项目详情失败");
            configuration.getLogger().print(e.getMessage());
        }
    }

    private void show(InputStream inputStream) throws Exception {
        this.frame = new JFrame("项目详情");
        this.frame.setContentPane(projectDetail);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(new Dimension(680, 680));
        this.frame.pack();
        this.frame.setLocation(400, 400);
        this.frame.setVisible(true);
        projectConfig.append(FileUtils.readStream(inputStream));

    }

}
