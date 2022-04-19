/*
package com.hqy.coll.gateway.config;

import com.hqy.fundation.common.base.lang.MqConstants;
import com.hqy.fundation.common.swticher.InternalGeneralSwitcher;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

import java.util.Map;

*/
/**
 * @author qy
 * @create 2021/8/19 23:37
 *//*

//@Configuration
@Deprecated
public class GatewayQueueConfig {

    */
/**
     * 注册采集服务用到的FanoutExchange
     * @return
     *//*

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(MqConstants.AMQP_COLL_EXCHANGE, true, false);
    }

    */
/**
     * 注册gateway用到的Queue
     * @return
     *//*

    @Bean
    public Queue gatewayQueue() {
        if (InternalGeneralSwitcher.ENABLE_COLL_TTL_MESSAGE_QUEUE.isOff()) {
            return new Queue(MqConstants.AMQP_GATEWAY_QUEUE, true);
        } else {
            Map<String, Object> args = new HashedMap<>();
            //给队列设置过期时间
            args.put(MqConstants.X_MESSAGE_TTL_KEY, MqConstants.QUEUE_TTL);
            //绑定死信交换机
            args.put(MqConstants.X_DEAD_LETTER_EXCHANGE, MqConstants.DIRECT_DEATH_EXCHANGE);
            //绑定死信交换机的routing key
            args.put(MqConstants.X_DEAD_LETTER_ROUTING_KEY, MqConstants.DEATH_ROUTING_KEY);
            return new Queue(MqConstants.AMQP_GATEWAY_QUEUE, true, false, false, args);
        }

    }

    */
/**
     * 将gateway队列绑定到采集服务的exchange中
     * @return
     *//*

    @Bean
    public Binding gatewayBinding() {
        return BindingBuilder.bind(gatewayQueue()).to(fanoutExchange());
    }


    @Bean
    public DirectExchange deathExchange() {
        return new DirectExchange(MqConstants.DIRECT_DEATH_EXCHANGE, true, false);
    }

    @Bean
    public Queue deathQueue() {
        return new Queue(MqConstants.DIRECT_DEATH_QUEUE, true, false, false);
    }


    @Bean
    public Binding deathBinds() {
        return BindingBuilder.bind(deathQueue()).to(deathExchange()).with(MqConstants.DEATH_ROUTING_KEY);
    }



}
*/
