package com.demo.ibatx.ui.module;

import com.alibaba.fastjson.JSONObject;
import com.demo.ibatx.generator.constant.LoggerFactory;
import com.demo.ibatx.generator.template.TableDetail;
import com.demo.ibatx.ui.Configuration;
import com.demo.ibatx.util.FileUtils;
import com.demo.ibatx.util.OsUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableModule extends JPanel implements ModuleInitializer {

    private Configuration configuration;

    private List<TableDetail> tableDetailList;

    public static final String TABLE_FILE_TMP = "config/table-tmp.properties";

    private static final String TABLE_FILE_JSON = "tableList.json";

    private static final String TABLE_FILE_PATH = "table";

    public TableModule(Configuration configuration) {
        this.configuration = configuration;
        this.tableDetailList = new ArrayList<>();
        this.add(new TableButton(null, this, configuration));
    }

    @Override
    public void initialize() {
        try {
            File fileDir = new File(this.configuration.getBaseDir() + OsUtil.getFileSep() + TABLE_FILE_PATH);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file = new File(this.configuration.getBaseDir() + OsUtil.getFileSep() + TABLE_FILE_PATH + OsUtil.getFileSep() + TABLE_FILE_JSON);
            if (!file.exists()) {
                file.createNewFile();
            }
            String tableJson = FileUtils.readFile(file);
            if (StringUtils.hasLength(tableJson)) {
                this.tableDetailList.addAll(JSONObject.parseArray(tableJson, TableDetail.class));
            }
            if (!CollectionUtils.isEmpty(this.tableDetailList)) {
                for (TableDetail tableDetail : this.tableDetailList) {
                    this.add(new TableButton(tableDetail.getTableName(), this, this.configuration));
                }
            }
            super.updateUI();
        } catch (Exception e) {
            LoggerFactory.getLogger().print(e.getMessage());
            JOptionPane.showMessageDialog(null, "创建table文件夹失败或者格式错误", "提示", JOptionPane.ERROR_MESSAGE);
        }
        this.configuration.setTableList(this.tableDetailList);
    }

    public void removeTable(TableButton tableButton) {
        this.tableDetailList.removeIf(t -> Objects.equals(t.getTableName(), tableButton.getTableName()));
        this.remove(tableButton);
        this.updateUI();
    }

    public void removeTable(TableDetail tableDetail) {
        this.tableDetailList.remove(tableDetail);
        Component[] components = this.getComponents();
        for (Component component : components) {
            if (component instanceof TableButton
                    && Objects.equals(((TableButton) component).getTableName(), tableDetail.getTableName())) {
                this.remove(component);
                return;
            }
        }
    }

    public void addTable(TableDetail tableDetail) {
        if (this.tableDetailList.contains(tableDetail)) {
            removeTable(tableDetail);
        }
        this.tableDetailList.add(tableDetail);
        this.add(new TableButton(tableDetail.getTableName(), this, this.configuration));
        this.updateUI();
    }

    public InputStream getTmpFile() throws Exception {
        return FileUtils.getResourceAsStream(TABLE_FILE_TMP);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        try {
            if (Objects.nonNull(this.tableDetailList)) {
                File file = new File(this.configuration.getBaseDir() + OsUtil.getFileSep() + TABLE_FILE_PATH + OsUtil.getFileSep() + TABLE_FILE_JSON);
                FileUtils.writeFile(file, JSONObject.toJSONString(this.tableDetailList));
            }
        } catch (Exception e) {
            this.configuration.getLogger().print("保存文件失败" + e.getMessage());
        }

    }
}
