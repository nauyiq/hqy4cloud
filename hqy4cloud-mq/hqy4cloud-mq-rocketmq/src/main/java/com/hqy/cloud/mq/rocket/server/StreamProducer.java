package com.hqy.cloud.mq.rocket.server;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.hqy.cloud.stream.common.StreamConstants;
import com.hqy.cloud.stream.core.MessageBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;

/**
 * 给予spring cloud stream的stream消息生产者
 * @author qiyuan.hong
 * @date 2024/8/16
 */
@Slf4j
@RequiredArgsConstructor
public class StreamProducer {
    private final StreamBridge streamBridge;

    /**
     * 发送消息
     * @param bindingName 绑定的名称，主题
     * @param tag         标签
     * @param message     要发送的消息内容
     * @return            是否发送成功
     */
    public boolean send(String bindingName, String tag, String message) {
        // 构建消息对象
        MessageBody body = MessageBody.create(IdUtil.fastSimpleUUID(), message);
        log.info("Send stream message, {}, {}", bindingName, tag);
        boolean result = streamBridge.send(bindingName, MessageBuilder.withPayload(body).setHeader(StreamConstants.TAGS, tag).build());
        if (!result) {
            log.warn("Failed execute to send stream message, {}, {}", bindingName, tag);
        }
        return result;
    }

    public boolean send(String bindingName, String tag, String message, Map<String, String> headers) {
        // 构建消息对象
        MessageBody body = MessageBody.create(IdUtil.fastSimpleUUID(), message);
        log.info("Send stream message, {}, {}, {}", bindingName, tag, JSON.toJSONString(headers));
        MessageBuilder<MessageBody> builder = MessageBuilder.withPayload(body);
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(builder::setHeader);
        }
        builder.setHeader(StreamConstants.TAGS, tag);
        boolean result = streamBridge.send(bindingName, builder.build());
        if (!result) {
            log.warn("Failed execute to send stream message, {}, {}", bindingName, tag);
        }
        return result;
    }



}
