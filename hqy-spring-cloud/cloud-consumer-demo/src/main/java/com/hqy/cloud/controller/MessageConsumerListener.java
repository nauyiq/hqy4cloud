package com.hqy.cloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author qy
 * @create 2021/8/2 23:50
 */
@Component
@EnableBinding(Sink.class)
@Slf4j
public class MessageConsumerListener {

    @Value("${env}")
    private String env;

    @StreamListener(Sink.INPUT)
    public void input(Message<String> message) {
        log.info("### 当前环境 -> {}, 接收到消息 -> {}", env, message.getPayload());
    }

}
