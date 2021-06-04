package com.demo.sdk.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class AppListener {

    private static final Logger logger = LoggerFactory.getLogger(AppListener.class);

    static void start() {
        for (String item : ConfigContext.getApplication()) {
            logger.info("apollo start listen namespace : {}", item);
            Config config = ConfigService.getConfig(item);
            config.addChangeListener(changeEvent -> {
                Set<String> keys = changeEvent.changedKeys();
                for (String key : keys) {
                    ConfigChange configChange = changeEvent.getChange(key);
                    logger.info("应用配置更新 key = {}, old = {}, new = {}",
                            key, configChange.getOldValue(), configChange.getNewValue());
                }
            });
        }
    }


}
