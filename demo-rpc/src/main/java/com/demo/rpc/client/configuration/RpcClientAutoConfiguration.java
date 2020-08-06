package com.demo.rpc.client.configuration;

import com.demo.rpc.client.ServiceUrlSelector;
import com.demo.rpc.client.utils.RpcClientNameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;

/**
 * 该类用于解决rpc调用时因为初始化loadbanace导致的超时问题, 处理方法为, 程序启动时初始化所有client
 */
public class RpcClientAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(RpcClientAutoConfiguration.class);

    //@Configuration
    static class RpcClientAutoInit implements SmartLifecycle {
        boolean isRunning = false;

        @Autowired
        ServiceUrlSelector serviceUrlSelector;

        @Override
        public boolean isAutoStartup() {
            return true;
        }

        @Override
        public void stop(Runnable callback) {
            isRunning = false;
        }

        @Override
        public void start() {
            logger.info("Starting rpc-clients init..");
            for (String name : RpcClientNameManager.getNames()) {
                try {
                    serviceUrlSelector.selectUrl(name);
                } catch (Exception e) {
                    logger.error("Init rpc-clients fail with [name] = {}", name);
                }
            }
            isRunning = true;
        }

        @Override
        public void stop() {
            isRunning = false;
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public int getPhase() {
            return Ordered.LOWEST_PRECEDENCE;
        }

    }

}