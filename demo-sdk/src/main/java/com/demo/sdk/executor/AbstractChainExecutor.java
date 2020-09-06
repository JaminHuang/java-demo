package com.demo.sdk.executor;

/**
 * 执行器
 */
public abstract class AbstractChainExecutor<T> {

    /**
     * 处理业务
     */
    protected abstract T execute(T t);

    /**
     * 是否需要处理
     */
    protected boolean shouldExecute(T t) {

        return true;
    }


}
