package com.demo.sdk.mq;

/**
 * 消费状态
 */
public enum ConsumeStatus {
    // 消费成功
    SUCCESS,
    // 消费失败
    FAIL,
    // 等会重新消费（只针对PTP消费者）
    RECONSUME_LATER,
    // 未知
    UN_KNOWN;
}