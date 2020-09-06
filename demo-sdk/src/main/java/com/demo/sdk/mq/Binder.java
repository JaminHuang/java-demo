package com.demo.sdk.mq;

import com.demo.sdk.util.ContextUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * 此类用于动态声明交换机，队列，以及绑定队列到交换机
 * ptp是消息发送的时候检查绑定
 * broadcast是启动时绑定：对于广播消息来说，没有消费者的消息发送是无意义的
 */
public class Binder {

    private static Set<String> directs = new HashSet<>();

    /**
     * 声明队列，绑定队列到直流、直流延时交换机
     *
     * @param queue
     * @throws IOException
     * @throws TimeoutException
     */
    static void bindDirect(String queue, String routingKey) throws IOException, TimeoutException {
        if (directs.contains(queue)) {
            return;
        }
        Channel channel = ContextUtils.getConnectionFactory().createConnection().createChannel(false);
        // 声明队列
        channel.queueDeclare(queue, true, false, false, null);
        // 绑定到直流交换机
        channel.queueBind(queue, RabbitMqConfig.getDirectExchange(), routingKey);
        // 绑定到延时直流交换机
        channel.queueBind(queue, RabbitMqConfig.getDirectDelayExchange(), routingKey);
        channel.close();
        directs.add(queue);
    }

    /**
     * 声明队列，绑定队列到主题、主题延时交换机
     *
     * @param queue
     * @throws IOException
     * @throws TimeoutException
     */
    static void bindTopic(String queue, String routingKey) throws IOException, TimeoutException {
        Channel channel = ContextUtils.getConnectionFactory().createConnection().createChannel(false);
        // 声明队列
        channel.queueDeclare(queue, true, false, false, null);
        // 绑定到topic交换机
        channel.queueBind(queue, RabbitMqConfig.getTopicExchange(), routingKey);
        // 绑定到延时topic交换机
        channel.queueBind(queue, RabbitMqConfig.getTopicDelayExchange(), routingKey);
        channel.close();
    }

    /**
     * 声明交换机
     *
     * @param exchange
     * @param delayedExchange
     * @param queue
     * @throws IOException
     * @throws TimeoutException
     */
    static void bindFanout(String exchange, String delayedExchange, String queue) throws IOException, TimeoutException {
        Channel channel = ContextUtils.getConnectionFactory().createConnection().createChannel(false);
        // 声明fanout交换机
        channel.exchangeDeclare(exchange, "fanout", true, false, null);

        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "fanout");
        // 声明延时fanout交换机
        channel.exchangeDeclare(delayedExchange, "x-delayed-message", true, false, args);
        // 声明队列
        channel.queueDeclare(queue, true, false, false, null);
        // 绑定到fanout交换机
        channel.queueBind(queue, exchange, "");
        // 绑定到fanout延时交换机
        channel.queueBind(queue, delayedExchange, "");
        channel.close();
    }


}