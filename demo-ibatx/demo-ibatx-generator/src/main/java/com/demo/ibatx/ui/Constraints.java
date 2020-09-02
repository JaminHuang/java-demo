package com.demo.ibatx.ui;

import java.awt.*;

public class Constraints extends GridBagConstraints {

    /**
     * 初始化左上角位置
     *
     * @param gridx
     * @param gridy
     */
    public Constraints(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
    }

    /**
     * 初始化左上角位置和所占行数和列数
     *
     * @param gridx
     * @param gridy
     * @param gridwidth
     * @param gridheight
     */
    public Constraints(int gridx, int gridy, int gridwidth, int gridheight) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
    }

    /**
     * 对齐方式
     *
     * @param anchor
     * @return
     */
    public Constraints setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    /**
     * 是否拉伸及拉伸方向
     *
     * @param fill
     * @return
     */
    public Constraints setFill(int fill) {
        this.fill = fill;
        return this;
    }

    /**
     * x和y方向上的增量
     *
     * @param weightx
     * @param weighty
     * @return
     */
    public Constraints setWeight(double weightx, double weighty) {
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    /**
     * 外部填充
     *
     * @param distance
     * @return
     */
    public Constraints setInsets(int distance) {
        this.insets = new Insets(distance, distance, distance, distance);
        return this;
    }

    /**
     * @param top
     * @param left
     * @param bottom
     * @param right
     * @return
     */
    public Constraints setInsets(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    /**
     * 内填充
     *
     * @param ipadx
     * @param ipady
     * @return
     */
    public Constraints setIpad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }
}
