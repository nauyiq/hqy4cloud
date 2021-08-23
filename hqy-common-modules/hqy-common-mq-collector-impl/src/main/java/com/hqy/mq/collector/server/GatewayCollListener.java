package com.hqy.mq.collector.server;

import com.hqy.common.base.lang.MqConstants;
import com.hqy.mq.collector.entity.ThrottledIpBlock;
import com.hqy.mq.collector.service.CollPersistService;
import com.hqy.util.JsonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qy
 * @create 2021/8/23 21:34
 */
@RabbitListener(queues = {MqConstants.AMQP_GATEWAY_QUEUE})
@Component
public class GatewayCollListener {

    @Resource
    private CollPersistService persistService;

    @RabbitHandler
    public void collGatewayThrottleIpDataHandler(String throttledIpBlockJson) {
        persistService.saveThrottledIpBlockHistory(JsonUtil.toBean(throttledIpBlockJson, ThrottledIpBlock.class));
    }

}
