package com.demo.sdk.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Runner implements Comparable<Runner> {

    protected Logger logger;

    public Runner(){
        logger = LoggerFactory.getLogger(getClass());
    }
    /**
     * 执行具体内容
     *
     * @param
     * @author caiLinFeng
     * @date 2018年11月26日
     */
    public abstract void execute();

    /**
     * 执行顺序，从0开始计数
     *
     * @param
     * @author caiLinFeng
     * @date 2018年11月26日
     */
    public abstract int getOrder();

    @Override
    public int compareTo(Runner o) {
        return this.getOrder() - o.getOrder();
    }

}
