package com.demo.sdk.mq;

import com.demo.sdk.runner.DestroyRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author caiLinFeng
 * @Key 属性初始化
 * @date 2018年11月27日
 */
@Component
public class MqEndRunner extends DestroyRunner {

    private static final Logger logger = LoggerFactory.getLogger(MqEndRunner.class);

    private static final long waitTime = 120;

    @Autowired
    private MessageContainer messageContainer;

    @Override
    public void execute() {
        logger.info("----------->MQ stop");
        messageContainer.stop();
        try {
            if (!MessageContainer.awaitTermination(waitTime)) {
                logger.error("MQ stop fail, 进程在{}秒内无法结束，尝试强制结束", waitTime);
            } else {
                logger.info("MQ stop success");
            }
        } catch (InterruptedException e) {
            logger.error("MQ 等待线程被打断");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
