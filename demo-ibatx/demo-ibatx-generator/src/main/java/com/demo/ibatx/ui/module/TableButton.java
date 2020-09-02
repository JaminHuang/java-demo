package com.demo.ibatx.ui.module;

import com.demo.ibatx.ui.ColorConstant;
import com.demo.ibatx.ui.Configuration;
import org.springframework.util.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TableButton extends JPanel {

    private String tableName;

    private JButton operButton;

    private TableModule tableModule;

    private Configuration configuration;


    public TableButton(String tableName,TableModule tableModule,Configuration configuration) {
        this.tableModule = tableModule;
        this.tableName = tableName;
        this.configuration = configuration;

        this.setLayout(new GridLayout(0, 1));
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.setPreferredSize(new Dimension(88, 66));
        this.setBackground(ColorConstant.Gainsboro);


        this.operButton = new JButton();
        if(StringUtils.hasLength(tableName)){
            this.add(new JLabel(tableName));
            this.operButton = new JButton("删除");
            this.operButton.addActionListener(e->{
                this.tableModule.removeTable(this);
            });
        } else {
            this.operButton = new JButton("增加");
            this.operButton.setBackground(ColorConstant.Gainsboro);
            this.operButton.addActionListener(e->{
                new TableDetailEditor(tableModule,this.configuration).show();
            });
        }

        this.add(operButton);

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
