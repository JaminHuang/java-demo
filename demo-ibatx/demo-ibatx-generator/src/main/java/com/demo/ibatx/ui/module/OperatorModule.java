package com.demo.ibatx.ui.module;

import com.demo.ibatx.generator.Generator;
import com.demo.ibatx.generator.constant.LoggerFactory;
import com.demo.ibatx.ui.Configuration;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OperatorModule extends JPanel implements ModuleInitializer {

    private JButton generatorButton;

    private Configuration configuration;

    private ExecutorService executorService;

    public OperatorModule(Configuration configuration) {
        this.configuration = configuration;
        this.generatorButton = new JButton("生成");

        this.add(generatorButton);
    }

    @Override
    public void initialize() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.generatorButton.addActionListener(e -> {
            Future<String> future = executorService.submit(() -> {
                Generator generator = new Generator();
                try {
                    generator.generator(this.configuration.getBaseDir(), this.configuration.getProject(), this.configuration.getTableList());
                } catch (Exception e1) {
                    this.configuration.getLogger().print(e1.getMessage());
                    e1.printStackTrace();
                }
                return "SUCCESS";
            });

            try {
                String result = future.get(5, TimeUnit.SECONDS);
                if ("SUCCESS".equals(result)) {
                    JOptionPane.showMessageDialog(null, "生成成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e1) {
                LoggerFactory.getLogger().print("关闭线程：" + future.cancel(true));
                JOptionPane.showMessageDialog(null, "生成超时", "提示", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
