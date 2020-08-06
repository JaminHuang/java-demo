package com.demo.sdk.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * web容器管理
 */
public class WebContainer {

    private static final Logger logger = LoggerFactory.getLogger(WebContainer.class);
    /**
     * 当前容器运行与否
     */
    private volatile static boolean runing = true;
    /**
     * 容器正在处理的http数
     */
    private static AtomicLong count = new AtomicLong(0);

    public static boolean isRuning() {
        return runing;
    }

    public static void pause() {
        WebContainer.runing = false;
    }

    public static long incrementAndGet() {
        return count.incrementAndGet();
    }

    public static long decrementAndGet() {
        return count.decrementAndGet();
    }

    public static boolean awaitTermination(long timeout) throws InterruptedException {
        while (timeout >= 0) {
            if (count.get() == 0) {
                return true;
            }
            logger.info("当前还有{}个未完成的http请求", count.get());
            timeout -= 1;
            Thread.sleep(1000);
        }
        return false;
    }


}