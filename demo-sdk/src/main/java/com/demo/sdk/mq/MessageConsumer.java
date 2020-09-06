package com.demo.sdk.mq;

import com.demo.sdk.exception.IllegalMqException;
import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.*;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 消息消费者
 *
 * @param <T>
 */
public abstract class MessageConsumer<T> {

    protected Logger logger;

    private SimpleMessageListenerContainer container;

    String queue;

    @Autowired
    protected RedisUtils redisUtils;

    @Autowired
    private org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory;

    /**
     * 重试间隔
     */
    static Map<Integer, Long> reConsumeIntervalMap = new HashMap<>();
    /**
     * 最大重试次数
     */
    static int maxConsumeTimes;

    static {
        reConsumeIntervalMap.put(1, 2 * 1000L);
        reConsumeIntervalMap.put(2, 5 * 1000L);
        reConsumeIntervalMap.put(3, 10 * 1000L);
        reConsumeIntervalMap.put(4, 20 * 1000L);
        reConsumeIntervalMap.put(5, 30 * 1000L);
        reConsumeIntervalMap.put(6, 60 * 1000L);
        reConsumeIntervalMap.put(7, 90 * 1000L);
        reConsumeIntervalMap.put(8, 120 * 1000L);
        reConsumeIntervalMap.put(9, 150 * 1000L);
        reConsumeIntervalMap.put(10, 180 * 1000L);
        reConsumeIntervalMap.put(11, 360 * 1000L);
        reConsumeIntervalMap.put(12, 720 * 1000L);
        reConsumeIntervalMap.put(13, 840 * 1000L);
        reConsumeIntervalMap.put(14, 1200 * 1000L);
        reConsumeIntervalMap.put(15, 1800 * 1000L);
        reConsumeIntervalMap.put(16, 3600 * 1000L);
        maxConsumeTimes = reConsumeIntervalMap.size();
    }

    public MessageConsumer() {
        logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * 启动监听
     */
    void startListen() throws IOException, TimeoutException {
        // 检查路由
        check();
        // 确定队列
        queue();
        // 绑定队列
        bind();
        // 配置
        container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queue);
        container.setConcurrentConsumers(consumers());

        // PTP消息手动确认
        if (MessageModel.BROADCAST.equals(messageModel())) {
            // 广播消息不确认，提高吞吐量
            container.setAcknowledgeMode(AcknowledgeMode.NONE);
        } else {
            container.setPrefetchCount(prefetchCount());
            container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        }
        container.setMessageListener(new MyChannelAwareMessageListener());
        container.start();
        logger.info("-----------> {} start listen!!!", this.getClass().getSimpleName());
    }

    void stopListen() {
        if (container != null) {
            container.stop();
        }
        logger.info("-----------> {} stop listen!!!", this.getClass().getSimpleName());
    }

    private void check() {
        if (subscribe() == null) {
            String clazz = this.getClass().getName();
            throw new IllegalMqException("subscribe() of " + clazz + " is null");
        }
    }

    abstract void queue();

    abstract void bind() throws IOException, TimeoutException;

    /**
     * 订阅消息路由
     *
     * @return
     */
    protected abstract MessageRoute subscribe();

    /**
     * 消费模式
     *
     * @return
     */
    abstract MessageModel messageModel();

    void reconsume(ConsumeStatus status, Message message) {

    }

    protected abstract ConsumeStatus consume(Message<T> message, T data);

    /**
     * 消费者个数
     * 默认服务器核心数
     *
     * @return
     */
    protected int consumers() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 可以获取消息的个数
     *
     * @return
     */
    protected int prefetchCount() {
        return 200;
    }

    /**
     * 过期，使用consumers()
     * @return
     */
    @Deprecated
    protected int threads() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Deprecated
    protected void runAsyn(Message<T> message) {

    }

    private ConsumeStatus run(Message<T> message) {
        long start = System.currentTimeMillis();
        ConsumeStatus status = ConsumeStatus.UN_KNOWN;
        try {
            T data = message.getData();
            status = consume(message, data);
        } catch (Throwable e) {
            // 抛出异常视为消费失败
            status = ConsumeStatus.FAIL;
            logger.error("消息消费异常, [error] = {}", ExceptionUtils.getExceptionMsg(e));
        }
        long end = System.currentTimeMillis();
        logger.info("mq执行耗时{}ms", end - start);
        return status;
    }

    class MyChannelAwareMessageListener implements ChannelAwareMessageListener {

        /**
         * 确认消息
         *
         * @param message1
         * @param channel
         */
        private void ack(org.springframework.amqp.core.Message message1, Channel channel) {
            if (MessageModel.BROADCAST.equals(MessageConsumer.this.messageModel())) {
                return;
            }
            // ptp,topic手动确认
            try {
                channel.basicAck(message1.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                logger.error("消息确认异常, [error] = {}", ExceptionUtils.getExceptionMsg(e));
            }
        }

        /**
         * 消费消息
         *
         * @param message1
         * @param channel
         */
        @Override
        public void onMessage(org.springframework.amqp.core.Message message1, Channel channel) {
            Message message = null;
            // 转换消息
            try {
                message = (Message) RabbitMqConfig.getMessageConverter().fromMessage(message1);
            } catch (Throwable e) {
                // 反序列化异常
                logger.error("deserialize error, [error] = {}", ExceptionUtils.getExceptionMsg(e));
                ack(message1, channel);
                MessageProducer.fail(message1);
                return;
            }
            // 计数
            MessageContainer.incr();

            ReqThreadLocal.setTid(message.getTid());

            // 处理消息
            ConsumeStatus status = run(message);

            // 确认消息是否需要重新消费
            try {
                reconsume(status, message);
            } catch (Throwable e) {
                logger.error("reconsume error, [error] = {}", ExceptionUtils.getExceptionMsg(e));
            }

            ack(message1, channel);

            ReqThreadLocal.removeAll();

            MessageContainer.decr();
        }
    }
}