package com.demo.rpc.core.listener;

import com.demo.rpc.client.ServiceUrlSelector;
import com.demo.rpc.client.utils.RpcClientNameManager;
import com.demo.rpc.core.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * 该类用于解决rpc调用时因为初始化loadbanace导致的超时问题, 处理方法为, 程序启动时初始化所有client.
 */
@Configuration
public class KylinApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(KylinApplicationReadyListener.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Starting rpc-clients init..");
        ServiceUrlSelector serviceUrlSelector = SpringContext.getApplicationContext().getBean(ServiceUrlSelector.class);
        for (String name : RpcClientNameManager.getNames()) {
            try {
                serviceUrlSelector.selectUrl(name);
            } catch (Exception e) {
                logger.error("Init rpc-clients fail with [name] = {},[error] = {}", name, e.getMessage());
            }
        }

    }

}