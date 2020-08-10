package com.demo.sdk.mq;

import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 消息Model
 *
 * @param <T>
 */
@Data
public class Message<T> implements Serializable {
    /**
     * 消息ID
     */
    private String id;
    /**
     * 消息内容
     */
    private T data;
    /**
     * 消息来源哪个应用
     */
    private String source;
    /**
     * 消息来源IP
     */
    private String sourceIP;
    /**
     * 消息创建时间，毫秒
     */
    private Long createTime;
    /**
     * 消息接收时间，毫秒
     */
    private Long receiveTime;
    /**
     * 消费次数
     */
    private int consumeTimes;
    /**
     * 消息投递模式
     */
    private MessageModel messageModel;
    /**
     * 消息类型
     */
    private MessageType messageType;

    private String topic;

    private String tag;

    private String tid;

    public Message() {

    }

    public Message(MessageRoute messageRoute, T data) {
        Assert.notNull(messageRoute, "messageRoute is null");
        Assert.notNull(data, "data is null");
        this.topic = messageRoute.topic();
        this.tag = messageRoute.tag();
        this.data = data;
    }
}
