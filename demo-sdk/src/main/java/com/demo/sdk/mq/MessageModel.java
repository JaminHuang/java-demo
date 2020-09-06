package com.demo.sdk.mq;

public enum MessageModel {
    // 点对点消息（只有一个消费者能消费消息）
    PTP,
    // 主题消息（可以有多个消费者订阅消息）
    TOPIC,
    // 广播消息（集群内同一个应用都能收到消息）
    BROADCAST
}