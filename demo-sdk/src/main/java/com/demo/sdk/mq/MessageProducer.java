package com.demo.sdk.mq;

import com.demo.sdk.exception.BizException;
import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.ContextUtils;
import com.demo.sdk.util.ExceptionUtils;
import com.demo.sdk.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 消息生产者
 */
public class MessageProducer {

    protected static Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    private static String localIP = "unknown";

    /**
     * 发送PTP消息
     *
     * @param message
     */
    public static Message<?> ptp(Message<?> message) {
        message.setMessageType(MessageType.NORMAL);
        return ptp(message, null, null);
    }

    /**
     * 发送PTP延时消息
     *
     * @param message
     * @param delay   毫秒
     */
    public static Message<?> ptp(Message<?> message, Long delay) {
        message.setMessageType(MessageType.TIMER);
        return ptp(message, null, delay);
    }

    /**
     * 发送PTP延时消息
     *
     * @param message
     * @param queue   指定队列，为空则取topic.tag
     * @param delay   毫秒
     */
    static Message<?> ptp(Message<?> message, String queue, Long delay) {
        long start = System.currentTimeMillis();
        preHandle(message);
        if (message.getMessageModel() == null) {
            message.setMessageModel(MessageModel.PTP);
        }
        boolean isDelay = delay != null && delay > 0;
        String exchange = isDelay ? RabbitMqConfig.getDirectDelayExchange() : RabbitMqConfig.getDirectExchange();
        queue = queue != null ? queue : String.format("%s.%s", message.getTopic(), message.getTag());
        try {
            Binder.bindDirect(queue, queue);
            ContextUtils.getRabbitTemplate().convertAndSend(
                    exchange,
                    queue,
                    message,
                    message1 -> {
                        if (isDelay) {
                            message1.getMessageProperties().setHeader(MessageProperties.X_DELAY, delay);
                        }
                        message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message1;
                    });
        } catch (Throwable e) {
            logger.error("发送ptp消息失败, [message] = {}, [error] = {}", message, ExceptionUtils.getExceptionMsg(e));
            throw new BizException("发送ptp消息失败");
        }
        logger.info("发送ptp消息--> 耗时{}ms, [message] = {}", (System.currentTimeMillis() - start), message);
        return message;
    }

    /**
     * 发送TOPIC消息
     *
     * @param message
     */
    public static Message<?> topic(Message<?> message) {
        message.setMessageType(MessageType.NORMAL);
        return topic(message, null);
    }

    /**
     * 发送TOPIC延时消息
     *
     * @param message
     * @param delay   毫秒
     */
    public static Message<?> topic(Message<?> message, Long delay) {
        long start = System.currentTimeMillis();
        preHandle(message);
        message.setMessageModel(MessageModel.TOPIC);
        boolean isDelay = delay != null && delay > 0;
        String exchange = isDelay ? RabbitMqConfig.getTopicDelayExchange() : RabbitMqConfig.getTopicExchange();
        try {
            String routingKey = String.format("%s.%s@.a", message.getTopic(), message.getTag());
            ContextUtils.getRabbitTemplate().convertAndSend(
                    exchange,
                    routingKey,
                    message,
                    message1 -> {
                        if (isDelay) {
                            message1.getMessageProperties().setHeader(MessageProperties.X_DELAY, delay);
                        }
                        message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message1;
                    });
        } catch (Throwable e) {
            logger.error("发送topic消息失败, [message] = {}, [error] = {}", message, ExceptionUtils.getExceptionMsg(e));
            throw new BizException("发送topic消息失败");
        }
        logger.info("发送topic消息--> 耗时{}ms, [message] = {}", (System.currentTimeMillis() - start), message);
        return message;
    }

    /**
     * 发送广播消息
     *
     * @param message
     */
    public static Message<?> broadcast(Message<?> message) {
        message.setMessageType(MessageType.NORMAL);
        return broadcast(message, null);
    }

    /**
     * 发送广播消息
     *
     * @param message
     * @param delay   毫秒
     */
    public static Message<?> broadcast(Message<?> message, Long delay) {
        long start = System.currentTimeMillis();
        preHandle(message);
        message.setMessageModel(MessageModel.BROADCAST);
        boolean isDelay = delay != null && delay > 0;
        String exchange = isDelay ? RabbitMqConfig.getFanoutDelayExchange(message.getTopic(), message.getTag())
                : RabbitMqConfig.getFanoutExchange(message.getTopic(), message.getTag());
        try {
            ContextUtils.getRabbitTemplate().convertAndSend(
                    exchange,
                    "",
                    message,
                    message1 -> {
                        if (isDelay) {
                            message1.getMessageProperties().setHeader(MessageProperties.X_DELAY, delay);
                        }
                        message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
                        return message1;
                    });
        } catch (Throwable e) {
            logger.error("发送broadcast消息失败, [message] = {}, [error] = {}", ExceptionUtils.getExceptionMsg(e));
            throw new BizException("发送broadcast消息失败");
        }
        logger.info("发送broadcast消息--> 耗时{}ms, [message] = {}", (System.currentTimeMillis() - start), message);
        return message;
    }

    /**
     * 发送到失败队列
     *
     * @param message
     * @return
     */
    public static void fail(Object message) {
        String routingKey = RabbitMqConfig.getFailLetterQueue();
        try {
            ContextUtils.getRabbitTemplate().convertAndSend(
                    RabbitMqConfig.getDirectExchange(),
                    routingKey,
                    message,
                    message1 -> {
                        message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        // 消费者名字
                        message1.getMessageProperties().setHeader("consumer", ContextUtils.getApplicationName());
                        return message1;
                    });
        } catch (Throwable e) {
            logger.error("发送到失败队列异常, [message] = {}, [error] = {}",
                    message, ExceptionUtils.getExceptionMsg(e));
        }
    }

    private static void preHandle(Message<?> message) {
        Assert.notNull(message, "message is null");
        Assert.notNull(message.getData(), "data is null");
        Assert.notNull(message.getTopic(), "topic is null");
        Assert.notNull(message.getTag(), "tag is null");
        long currentTime = System.currentTimeMillis();
        if (message.getMessageType() == null) {
            message.setMessageType(MessageType.TIMER);
        }
        if (message.getId() == null) {
            message.setId(StringUtils.generateUUID());
        }
        if (message.getCreateTime() == null) {
            message.setCreateTime(currentTime);
        }
        if (message.getSource() == null) {
            message.setSource(ContextUtils.getApplicationName());
        }
        if (message.getSourceIP() == null) {
            message.setSourceIP(localIP);
        }
        // 延迟消息消息体已经有tid了
        if (message.getTid() == null) {
            message.setTid(ReqThreadLocal.getTid());
        }
    }

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            if (address != null) {
                localIP = address.getHostAddress();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


}