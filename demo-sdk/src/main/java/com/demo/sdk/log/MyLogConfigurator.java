package com.demo.sdk.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import java.io.ByteArrayInputStream;

public class MyLogConfigurator implements ApplicationListener<ApplicationReadyEvent>, Ordered {
    // 只跑一次
    private static volatile boolean executed = false;

    private static final Object lock = new Object();

    /**
     * 默认namespace
     */
    private static final String _LOGBACK = "logback";
    /**
     * 默认key
     */
    private static final String _CONTENT = "content";


    private static final String _LOGGER = "logger";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Config config = ConfigService.getConfig(_LOGBACK);
        if (config == null) {
            return;
        }
        configure();
        // 保证只添加一次监听器
        synchronized (lock) {
            if (executed) {
                return;
            }
            config.addChangeListener(changeEvent -> {
                configure();
            });
            executed = true;
        }
    }

    private void configure() {
        Config config = ConfigService.getConfig(_LOGBACK);
        if (config == null) {
            return;
        }
        String content = config.getProperty(_CONTENT, null);
        if (content == null) {
            return;
        }
        String logger = config.getProperty(_LOGGER, "");

        content = content.replace("${logger}", logger);

        configure(content.getBytes());
    }

    /**
     * 定义logger配置
     *
     * @param bytes
     */
    private void configure(byte[] bytes) {
        synchronized (lock) {
            Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
            LoggerContext loggerContext = logger.getLoggerContext();
            loggerContext.reset();
            loggerContext.putObject(LoggingSystem.class.getName(), 1);
            JoranConfigurator configurator = new JoranConfigurator();
            try {
                configurator.setContext(loggerContext);
                configurator.doConfigure(new ByteArrayInputStream(bytes));
            } catch (JoranException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
