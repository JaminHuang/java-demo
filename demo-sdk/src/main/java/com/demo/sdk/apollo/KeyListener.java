package com.demo.sdk.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class KeyListener {

    private static final Logger logger = LoggerFactory.getLogger(KeyListener.class);

    static void start() {
        Config config = ConfigService.getConfig(ConfigContext.getConfig());
        logger.info("apollo start listen namespace : {}", ConfigContext.getConfig());
        config.addChangeListener(changeEvent -> {
            Set<String> keys = changeEvent.changedKeys();
            for (String key : keys) {
                ConfigChange configChange = changeEvent.getChange(key);
                KeyHolder.update(key, configChange.getNewValue());
                logger.info("业务配置更新 key = {}, old = {}, new = {}",
                        key, configChange.getOldValue(), configChange.getNewValue());
            }
        });
    }
}
