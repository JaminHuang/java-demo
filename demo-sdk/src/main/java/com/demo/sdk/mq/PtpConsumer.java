package com.demo.sdk.mq;


import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * ptp消息消费者
 *
 * @param <T>
 */
public abstract class PtpConsumer<T> extends MessageConsumer<T> {

    @Override
    public final MessageModel messageModel() {
        return MessageModel.PTP;
    }

    @Override
    public final void queue() {
        this.queue = String.format("%s.%s", subscribe().topic(), subscribe().tag());
    }

    @Override
    public final void bind() throws IOException, TimeoutException {
        Binder.bindDirect(queue, queue);
    }

    @Override
    public final void reconsume(ConsumeStatus status, Message message) {
        if (ConsumeStatus.SUCCESS.equals(status)) {
            return;
        }
        // 重试
        if (ConsumeStatus.RECONSUME_LATER.equals(status)) {
            int consumeTimes = message.getConsumeTimes();
            if (consumeTimes >= maxConsumeTimes) {
                status = ConsumeStatus.FAIL;
            } else {
                //发送到延迟队列，消费次数加1
                consumeTimes = consumeTimes + 1;
                message.setConsumeTimes(consumeTimes);
                MessageProducer.ptp(message, reConsumeIntervalMap.get(consumeTimes));
                logger.info("重新消费..[message] = {}", JSONObject.toJSONString(message));
                return;
            }
        }
        // 消费失败
        if (ConsumeStatus.FAIL.equals(status)) {
            MessageProducer.fail(message);
            logger.error("消费失败..[message] = {}", JSONObject.toJSONString(message));
        }
    }
}