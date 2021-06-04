package com.demo.sdk.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.demo.sdk.util.ContextUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 设置apollo参数
 */
public class ApolloArgsRunListener implements SpringApplicationRunListener, Ordered {

    // 只跑一次
    private static volatile boolean executed = false;

    private static final Object lock = new Object();

    public ApolloArgsRunListener(SpringApplication application, String[] args) {
        ContextUtils.setApplication(application);
    }

    @Override
    public void starting() {
        synchronized (lock) {
            if (executed) {
                return;
            }
            // 先从System Property（服务器上部署）
            Properties properties = System.getProperties();
            String appId = properties.getProperty("app.id");
            String env = properties.getProperty("env");
            if (appId == null || env == null) {
                // 再从application.properties读取（本地开发用）
                try {
                    Properties properties1 = getResource();
                    appId = appId == null ? properties1.getProperty("app.id") : appId;
                    env = env == null ? properties1.getProperty("env") : env;
                    System.setProperty("app.id", appId);
                    System.setProperty("env", env);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("application.properties read error");
                }
            }
            init();
            executed = true;
        }
    }

    /**
     * 读取apollo application
     */
    private void init() {
        System.out.println("----------->apollo 预读取");
        Config c = ConfigService.getAppConfig();
        String application = c.getProperty("application", null);
        String config = c.getProperty("config", null);
        ConfigContext.setApplication(application.split(","));
        ConfigContext.setConfig(config);
    }

    private Properties getResource() throws IOException {
        String location = "/application.properties";
        InputStream input = new ClassPathResource(location).getInputStream();
        Properties properties = new Properties();
        properties.load(input);
        return properties;
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {

    }

    @Override
    public int getOrder() {
        return -5;
    }

}
