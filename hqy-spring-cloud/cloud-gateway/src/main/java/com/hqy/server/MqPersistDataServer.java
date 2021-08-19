package com.hqy.server;

import com.hqy.common.base.lang.MqConstants;
import com.hqy.mq.collector.entity.ThrottledIpBlock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 采用direct模式的交换机，专门用于mq采集
 * 交换机名称:coll_direct_exchange
 * 将需要持久化采集的数据 放进mq中...
 * @author qy
 * @create 2021/8/19 23:15
 */
@Component
public class MqPersistDataServer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void persistBlockIpAction(ThrottledIpBlock throttledIpBlock) {
        rabbitTemplate.convertAndSend(MqConstants.AMQP_COLL_EXCHANGE, "", throttledIpBlock);
    }


}
