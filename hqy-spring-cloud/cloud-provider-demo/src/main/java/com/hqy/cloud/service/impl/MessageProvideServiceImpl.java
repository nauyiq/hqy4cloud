package com.hqy.cloud.service.impl;

import com.hqy.cloud.service.MessageProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author qy
 * @create 2021/8/2 22:06
 */
@EnableBinding(Source.class) //可以理解为是一个消息的发送管道的定义
@Slf4j
public class MessageProvideServiceImpl implements MessageProviderService {

    @Resource
    private MessageChannel output; //消息的发送管道

    @Override
    public void sendMessage() {
        String uuid = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(uuid).build());
        log.info("### send message uuid -> {}", uuid);
    }
}
