package com.demo.sdk.util;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ContextUtils {

    private static SpringApplication application;

    private static StringRedisTemplate stringRedisTemplate;

    private static ConnectionFactory connectionFactory;

    private static RabbitTemplate rabbitTemplate;

    private static String profiles;

    private static String applicationName;

    public static SpringApplication getApplication() {
        return application;
    }

    public static void setApplication(SpringApplication application) {
        ContextUtils.application = application;
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public static void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        ContextUtils.stringRedisTemplate = stringRedisTemplate;
    }

    public static ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public static void setConnectionFactory(ConnectionFactory connectionFactory) {
        connectionFactory = connectionFactory;
    }

    public static RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public static void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        ContextUtils.rabbitTemplate = rabbitTemplate;
    }

    public static String getProfiles() {
        return profiles;
    }

    public static void setProfiles(String profiles) {
        ContextUtils.profiles = profiles;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        ContextUtils.applicationName = applicationName;
    }
}
