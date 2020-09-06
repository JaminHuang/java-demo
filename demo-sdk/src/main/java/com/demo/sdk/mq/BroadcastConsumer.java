package com.demo.sdk.mq;

import com.demo.sdk.util.ContextUtils;
import com.demo.sdk.util.StringUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 广播消息消费者
 *
 * @param <T>
 */
public abstract class BroadcastConsumer<T> extends MessageConsumer<T> {

    private String exchange;

    private String delayedExchange;

    @Override
    public final MessageModel messageModel() {
        return MessageModel.BROADCAST;
    }

    @Override
    public final void queue() {
        this.queue = String.format("%s.%s@%s", subscribe().topic(), subscribe().tag(), StringUtils.getStringRandom(6));
    }

    @Override
    public final void bind() throws IOException, TimeoutException {
        // 交换机名
        String topic = subscribe().topic();
        String tag = subscribe().tag();
        exchange = RabbitMqConfig.getFanoutExchange(topic, tag);
        delayedExchange = RabbitMqConfig.getFanoutDelayExchange(topic, tag);
        Binder.bindFanout(exchange, delayedExchange, queue);
    }

    @Override
    public void stopListen() {
        super.stopListen();
        Channel channel = ContextUtils.getConnectionFactory().createConnection().createChannel(false);
        if (channel == null) {
            return;
        }
        // 删除队列
        if (queue != null) {
            logger.info("----------->queue delete, queue = {}", queue);
            try {
                channel.queueDelete(queue);
            } catch (IOException e) {
                ;
            }
        }
        // 删除交换机
        if (exchange != null) {
            logger.info("----------->exchange delete, exchange = {}", exchange);
            try {
                channel.exchangeDelete(exchange, true);
            } catch (IOException e) {
                ;
            }
        }
        // 删除交换机
        if (delayedExchange != null) {
            logger.info("----------->exchange delete, exchange = {}", delayedExchange);
            try {
                channel.exchangeDelete(delayedExchange, true);
            } catch (IOException e) {
                ;
            }
        }
        // 关闭channel
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }


}