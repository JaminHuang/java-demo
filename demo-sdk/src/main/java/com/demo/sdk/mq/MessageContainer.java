package com.demo.sdk.mq;

import com.demo.sdk.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MessageContainer {

    private static final Logger logger = LoggerFactory.getLogger(MessageContainer.class);
    /**
     * 当前任务数量
     */
    private static AtomicInteger count = new AtomicInteger(0);

    @Autowired(required = false)
    private Set<MessageConsumer> consumers;

    /**
     * 开始监听
     *
     * @throws IOException
     * @throws TimeoutException
     */
    void start() throws IOException, TimeoutException {
        if (consumers == null || consumers.isEmpty()) {
            return;
        }
        for (MessageConsumer consumer : consumers) {
            MessageConsumer target = consumer;
            boolean isProxy = AopUtils.isAopProxy(consumer);
            if (isProxy) {
                try {
                    target = (MessageConsumer) ((Advised) consumer).getTargetSource().getTarget();
                } catch (Exception e) {
                    logger.error("{}", ExceptionUtils.getExceptionMsg(e));
                }
            }
            target.startListen();
        }
    }

    /**
     * 停止监听
     */
    void stop() {
        if (consumers == null || consumers.isEmpty()) {
            return;
        }
        for (MessageConsumer consumer : consumers) {
            MessageConsumer target = consumer;
            boolean isProxy = AopUtils.isAopProxy(consumer);
            if (isProxy) {
                try {
                    target = (MessageConsumer) ((Advised) consumer).getTargetSource().getTarget();
                } catch (Exception e) {
                    logger.error("{}", ExceptionUtils.getExceptionMsg(e));
                }
            }
            target.stopListen();
        }
    }

    static void incr() {
        count.incrementAndGet();
    }

    static void decr() {
        count.decrementAndGet();
    }

    /**
     * 等待终止
     *
     * @param timeout
     * @return
     * @throws InterruptedException
     */
    public static boolean awaitTermination(long timeout) throws InterruptedException {
        // 线程休眠
        Thread.sleep(5000);
        while (timeout >= 0) {
            if (count.get() == 0) {
                return true;
            }
            logger.info("当前还有{}个未完成的message", count.get());
            timeout -= 1;
            Thread.sleep(1000);
        }
        return false;
    }
}
