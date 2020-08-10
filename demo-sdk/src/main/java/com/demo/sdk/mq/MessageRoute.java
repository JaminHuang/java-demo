package com.demo.sdk.mq;


public interface MessageRoute {
    String topic();

    String tag();

    String describe();
}
