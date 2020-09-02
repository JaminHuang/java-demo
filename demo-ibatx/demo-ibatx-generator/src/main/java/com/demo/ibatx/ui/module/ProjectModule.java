package com.demo.ibatx.ui.module;

import com.demo.ibatx.generator.constant.LoggerFactory;
import com.demo.ibatx.ui.Configuration;
import com.demo.ibatx.ui.data.Logger;
import com.demo.ibatx.util.FileUtils;
import com.demo.ibatx.util.OsUtil;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProjectModule extends JPanel implements ModuleInitializer {

    private static final String PROJECT_TMP_FILE = "config/project-tmp.properties";

    private static final String PROJECT_DIR = "project";

    public static final String FILE_FIX = ".properties";

    private JComboBox<String> projectList;

    private JButton updateButton;

    private JButton createButton;

    private Configuration configuration;

    private List<String> projectNameList;

    private Logger logger;

    public ProjectModule(Configuration configuration) {
        this.configuration = configuration;
        this.logger = configuration.getLogger();
        init();
    }

    private void init() {
        this.projectNameList = new ArrayList<>();
        this.projectList = new JComboBox<>();
        this.add(projectList);

        this.updateButton = new JButton("修改");
        this.add(updateButton);

        this.createButton = new JButton("新增");
        this.add(createButton);
    }

    private boolean checkTmpFile() {
        if (Objects.isNull(getTmpFile())) {
            JOptionPane.showMessageDialog(null, "工作目录缺少文件" + PROJECT_TMP_FILE, "提示", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    InputStream getTmpFile()  {
        try {
            return FileUtils.getResourceAsStream(PROJECT_TMP_FILE);
        } catch (Exception e) {
            LoggerFactory.getLogger().print(e.getMessage());
            return null;
        }
    }

    @Override
    public void initialize() {

        this.updateButton.addActionListener((e) -> {
            if (!checkTmpFile()) {
                return;
            }
            Object selectedFile = this.projectList.getSelectedItem();
            if (Objects.isNull(selectedFile)) {
                JOptionPane.showMessageDialog(null, "还没有可以修改的项目，请先创建一个", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new ProjectDetail(this, this.configuration).show(selectedFile.toString());
        });

        this.createButton.addActionListener((e) -> {
            if (!checkTmpFile()) {
                return;
            }
            new ProjectDetail(this, this.configuration).show();
        });

        String projectAbsFile = this.configuration.getBaseDir() + OsUtil.getFileSep() + PROJECT_DIR;

        File file = new File(projectAbsFile);
        if (!file.exists()) {
            try {
                if (!file.mkdirs()) {
                    JOptionPane.showMessageDialog(null, "创建project文件夹失败", "提示", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                this.configuration.getLogger().print(e.getMessage());
            }
        }

        File[] projects = file.listFiles();
        if (Objects.isNull(projects)) {
            return;
        }
        for (File project : projects) {
            this.addProject(project);
        }
        this.configuration.setProject(Objects.isNull(projectList.getSelectedItem()) ? "" : projectList.getSelectedItem().toString() + FILE_FIX);
        this.projectList.addItemListener(e -> {
            Object item = e.getItem();
            this.configuration.setProject(Objects.isNull(item) ? "" : item.toString()+FILE_FIX);
        });
    }

    public boolean containsProject(String projectName) {
        return this.projectNameList.contains(projectName);
    }

    public void addProject(File file) {
        String projectName = file.getName().replace(FILE_FIX, "");
        this.projectNameList.add(projectName);
        this.projectList.addItem(projectName);
    }

    public String getProjectFileDir() {
        return configuration.getBaseDir() + OsUtil.getFileSep() + PROJECT_DIR;
    }
}
