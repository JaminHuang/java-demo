package com.demo.ibatx.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Objects;

public class ModuleJPanel extends JPanel {

    private String title;

    private JPanel content;

    public ModuleJPanel(String title) {
        this.title = title;
        this.init();
    }

    public ModuleJPanel(String title, JPanel content) {
        this.title = title;
        this.content = content;
        this.init();
    }

    private void init() {
        this.setLayout(new GridBagLayout());
        JLabel label = new JLabel(this.title);
        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout(new GridBagLayout());
        if (Objects.nonNull(this.content)) {
            this.content.setBackground(ColorConstant.Gainsboro);
            JPanel contentBorder = new JPanel();
            contentBorder.setBackground(ColorConstant.Gainsboro);
            contentJPanel.add(contentBorder, new Constraints(0, 0, 4, 1).setAnchor(Constraints.WEST).setIpad(600, 0));
            contentJPanel.add(this.content, new Constraints(0, 1, 1, 1).setAnchor(Constraints.WEST).setIpad(10, 10));
        }
        contentJPanel.setBackground(ColorConstant.Gainsboro);
        JPanel upBord = new JPanel();
        this.add(upBord, new Constraints(0, 0, 10, 1).setIpad(700, 10));
        this.add(label, new Constraints(0, 1));

        JPanel border = new JPanel();
        this.add(border, new Constraints(0, 2, 10, 1).setIpad(700, 10));

        contentJPanel.setBorder(new LineBorder(ColorConstant.Gainsboro, 5, true));
        this.add(contentJPanel, new Constraints(0, 3, Constraints.REMAINDER, 2).setFill(Constraints.HORIZONTAL).setIpad(100, 20));
    }
}
