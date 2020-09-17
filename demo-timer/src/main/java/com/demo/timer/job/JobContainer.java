package com.demo.timer.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class JobContainer {

    private static final Logger logger = LoggerFactory.getLogger(JobContainer.class);

    /**
     * 是否暂停
     */
    private volatile static boolean work = true;
    /**
     * 当前任务数量
     */
    private static AtomicInteger count = new AtomicInteger(0);

    static void incr() {
        count.incrementAndGet();
    }

    static void decr() {
        count.decrementAndGet();
    }

    static boolean isWork() {
        return work;
    }

    private static void stop() {
        work = false;
    }

    public static boolean awaitTermination(long timeout) throws InterruptedException {
        stop();
        while (timeout >= 0) {
            if (count.get() == 0) {
                return true;
            }
            logger.info("当前还有{}个未完成的JOB ", count.get());
            timeout -= 1;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // empty
            }
        }
        return false;
    }
}
