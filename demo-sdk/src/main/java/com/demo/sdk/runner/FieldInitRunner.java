package com.demo.sdk.runner;

import com.demo.sdk.util.ContextUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 属性初始化runner
 */
@Component
public class FieldInitRunner extends BeforeRunner {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${spring.profiles.active}")
    String profiles;

    @Value("${spring.application.name}")
    String applicationName;

    @Override
    public void execute() {
        ContextUtils.setApplicationName(applicationName);
        ContextUtils.setConnectionFactory(connectionFactory);
        ContextUtils.setProfiles(profiles);
        ContextUtils.setRabbitTemplate(rabbitTemplate);
        ContextUtils.setStringRedisTemplate(stringRedisTemplate);
    }

    @Override
    public int getOrder() {
        return -Integer.MAX_VALUE;
    }

}
