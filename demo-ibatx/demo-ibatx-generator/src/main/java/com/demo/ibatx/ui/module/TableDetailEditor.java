package com.demo.ibatx.ui.module;

import com.demo.ibatx.generator.template.TableDetail;
import com.demo.ibatx.ui.Configuration;
import com.demo.ibatx.util.FileUtils;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class TableDetailEditor {

    private TableModule tableModule;

    private JTextArea tableConfig;

    private JPanel tableDetail;

    private JButton confirmButton;

    private Configuration configuration;

    private JFrame frame;

    public TableDetailEditor(TableModule tableModule, Configuration configuration) {
        this.tableModule = tableModule;
        this.configuration = configuration;
        this.init();
    }

    private void init() {
        this.tableDetail = new JPanel();
        this.tableDetail.setPreferredSize(new Dimension(480, 300));
        this.tableDetail.setLayout(new BorderLayout());

        this.tableConfig = new JTextArea();
        this.tableConfig.setPreferredSize(new Dimension(480, 300));
        this.tableConfig.setLineWrap(true);
        this.tableDetail.add(tableConfig, BorderLayout.CENTER);

        this.confirmButton = new JButton("确认");
        this.tableDetail.add(confirmButton, BorderLayout.SOUTH);

        this.confirmButton.addActionListener(e -> {
            String tableDetailText = this.tableConfig.getText();
            Properties properties = new Properties();
            try {
                properties.load(new ByteArrayInputStream(tableDetailText.getBytes()));
                TableDetail tableDetail = new TableDetail();
                tableDetail.setTableName(properties.getProperty(TableDetail.TABLE_NAME));
                tableDetail.setIdName(properties.getProperty(TableDetail.ID_NAME, ""));
                tableDetail.setVersionName(properties.getProperty(TableDetail.VERSION_NAME, ""));
                tableDetail.setLogicDeleteName(properties.getProperty(TableDetail.LOGIC_DELETE, ""));
                tableDetail.setPageable(properties.getProperty(TableDetail.PAGEABLE));
                tableDetail.setDomainObjectName(properties.getProperty(TableDetail.DOMAIN_NAME));
                tableDetail.setSchema(properties.getProperty(TableDetail.SCHEMA));
                Assert.notNull(tableDetail.getTableName(), "表明不能为空");
                Assert.notNull(tableDetail.getPageable(), "是否分页不能为空");
                configuration.getLogger().print("新增一个表" + tableDetail.getTableName());
                this.tableModule.addTable(tableDetail);
            } catch (IOException e1) {
                this.configuration.getLogger().print(e1.getMessage());
                JOptionPane.showMessageDialog(null, "文件格式错误" + TableModule.TABLE_FILE_TMP, "提示", JOptionPane.ERROR_MESSAGE);
            }
            this.frame.dispose();
        });
    }

    public void show() {
        this.frame = new JFrame("表");
        this.frame.setContentPane(this.tableDetail);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(new Dimension(680, 680));
        this.frame.pack();
        this.frame.setLocation(400, 400);
        this.frame.setVisible(true);
        try {
            tableConfig.append(FileUtils.readStream(tableModule.getTmpFile()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "工作目录缺少文件" + TableModule.TABLE_FILE_TMP, "提示", JOptionPane.ERROR_MESSAGE);
        }
    }
}
