package com.demo.sdk.runner;

import com.demo.sdk.support.WebContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class WebEndRunner extends DestroyRunner {

    private static final Logger logger = LoggerFactory.getLogger(WebEndRunner.class);

    private static final long waitTime = 120;

    @Override
    public void execute() {
        try {
            logger.info("Tomcat stop start");
            WebContainer.pause();
            if (!WebContainer.awaitTermination(waitTime)) {
                logger.error("Tomcat stop fail, 线程在{}秒内无法结束，尝试强制结束", waitTime);
            } else {
                logger.info("Tomcat stop success");
            }
        } catch (InterruptedException e) {
            logger.error("Tomcat 等待线程被打断");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int getOrder() {
        return -Integer.MAX_VALUE;
    }

}
