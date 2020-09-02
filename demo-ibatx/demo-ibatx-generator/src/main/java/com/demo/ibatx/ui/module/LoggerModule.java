package com.demo.ibatx.ui.module;

import com.demo.ibatx.ui.Configuration;
import com.demo.ibatx.ui.data.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoggerModule extends JPanel implements ModuleInitializer {

    private Configuration configuration;

    private Logger logger;

    private JTextArea textArea;

    public LoggerModule(Configuration configuration) {
        this.configuration = configuration;
        this.logger = configuration.getLogger();
        init();
    }

    private void init() {

        JPanel loggerPannel = new JPanel();
        loggerPannel.setBackground(Color.BLUE);
        loggerPannel.setBorder(new EmptyBorder(0, 0, 0, 0));
        loggerPannel.setLayout(new GridLayout(0, 1));
        loggerPannel.setPreferredSize(new Dimension(480, 100));

        this.textArea = new JTextArea();
        //在文本框上添加滚动条
        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setSize(new Dimension(680, 300));
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        textScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.logger.addLoggerOut(log -> {
            this.textArea.append(log);
        });
        loggerPannel.add(textScroll);
        this.add(loggerPannel);

    }

    @Override
    public void initialize() {

    }
}
