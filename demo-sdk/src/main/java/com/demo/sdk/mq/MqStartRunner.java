package com.demo.sdk.mq;

import com.demo.sdk.runner.AfterRunner;
import com.demo.sdk.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mq启动
 *
 * @author caiLinFeng
 * @date 2018年11月27日
 */
@Component
public class MqStartRunner extends AfterRunner {

    private static final Logger logger = LoggerFactory.getLogger(MqStartRunner.class);

    @Autowired
    private MessageContainer messageContainer;

    @Override
    public void execute() {
        logger.info("----------->MQ start");
        try {
            messageContainer.start();
        } catch (Throwable e) {
            logger.error("[error] = {}", ExceptionUtils.getExceptionMsg(e));
            throw new RuntimeException("MQ start failed");
        }
    }

    @Override
    public int getOrder() {
        return -Integer.MAX_VALUE;
    }

}
