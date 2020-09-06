package com.demo.sdk.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    protected static Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

    private static MessageConverter messageConverter;

    private static final String FAIL_LETTER_QUEUE = "fail_letter";

    private static final String DIRECT_EXCHANGE = "demo.direct";

    private static final String DIRECT_DELAY_EXCHANGE = "demo.direct.x-delayed";

    private static final String TOPIC_EXCHANGE = "demo.topic";

    private static final String TOPIC_DELAY_EXCHANGE = "demo.topic.x-delayed";

    private static final String FANOUT_EXCHANGE_PREFIX = "demo.fanout";

    private static final String FANOUT_DELAY_EXCHANGE_PREFIX = "demo.fanout.x-delayed";

    @Bean
    public MessageConverter messageConverter() {
        return messageConverter = new CustomJackson2JsonMessageConverter();
    }

    // 直流交换机
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false, null);
    }

    // 直流延时交换机
    @Bean
    public CustomExchange directDelayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DIRECT_DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    // 主题交换机
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false, null);
    }

    // 主题延时交换机
    @Bean
    public CustomExchange topicDelayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "topic");
        return new CustomExchange(TOPIC_DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue failLetterQueue() {
        return new Queue(FAIL_LETTER_QUEUE, true);
    }

    @Bean
    Binding bindingFailLetter() {
        return BindingBuilder.bind(failLetterQueue()).to(directExchange()).with(FAIL_LETTER_QUEUE);
    }

    public static MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public static String getDirectExchange() {
        return DIRECT_EXCHANGE;
    }

    public static String getDirectDelayExchange() {
        return DIRECT_DELAY_EXCHANGE;
    }

    public static String getTopicExchange() {
        return TOPIC_EXCHANGE;
    }

    public static String getTopicDelayExchange() {
        return TOPIC_DELAY_EXCHANGE;
    }

    public static String getFailLetterQueue() {
        return FAIL_LETTER_QUEUE;
    }

    public static String getFanoutExchange(String topic, String tag) {
        return String.format("%s#%s.%s", FANOUT_EXCHANGE_PREFIX, topic, tag);
    }

    public static String getFanoutDelayExchange(String topic, String tag) {
        return String.format("%s#%s.%s", FANOUT_DELAY_EXCHANGE_PREFIX, topic, tag);
    }


}
