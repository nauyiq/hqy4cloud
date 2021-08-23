package com.hqy.mq.collector.config;

import com.hqy.common.base.lang.MqConstants;
import com.hqy.common.swticher.CommonSwitcher;
import com.hqy.common.swticher.InternalGeneralSwitcher;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author qy
 * @create 2021/8/19 23:37
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 注册采集服务用到的FanoutExchange
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(MqConstants.AMQP_COLL_EXCHANGE, true, false);
    }

    /**
     * 注册gateway用到的Queue
     * @return
     */
    @Bean
    public Queue gatewayQueue() {
        if (InternalGeneralSwitcher.ENABLE_COLL_TTL_MESSAGE_QUEUE.isOff()) {
            return new Queue(MqConstants.AMQP_GATEWAY_QUEUE, true);
        } else {
            Map<String, Object> args = new HashedMap<>();
            args.put(MqConstants.X_MESSAGE_TTL_KEY, MqConstants.QUEUE_TTL);
            return new Queue(MqConstants.AMQP_GATEWAY_QUEUE, true, false, false, args);
        }


    }

    /**
     * 将gateway队列绑定到采集服务的exchange中
     * @return
     */
    @Bean
    public Binding gatewayBinding() {
        return BindingBuilder.bind(gatewayQueue()).to(fanoutExchange());
    }


}
