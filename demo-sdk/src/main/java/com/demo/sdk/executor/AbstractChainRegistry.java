package com.demo.sdk.executor;


import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行器注册器
 */
public abstract class AbstractChainRegistry<T> {

    private final List<AbstractChainExecutor> processors = new ArrayList<>();


    /**
     * 执行
     */
    public void execute(T t) {
        if (CollectionUtils.isEmpty(processors)) {
            return;
        }
        processors.stream()
                .filter(e -> e.shouldExecute(t))
                .forEach(e -> e.execute(t));
    }


    /**
     * 配置
     */
    @PostConstruct
    public void configExecutor() {
        initExecutor();
    }

    /**
     * 初始化
     */
    public abstract void initExecutor();

    /**
     * 添加执行器
     */
    protected void addExecutor(AbstractChainExecutor<T> processor) {
        if (processor != null) {
            processors.add(processor);
        }

    }

}
