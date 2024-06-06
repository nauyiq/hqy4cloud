package com.hqy.cloud.mq.rabbit.dynamic;

/**
 * Rabbitmq 交换机类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 9:38
 */
public enum ExchangeEnum {

    /**
     * 直连交换机
     * <p>
     * 根据routing-key精准匹配队列(最常使用)
     */
    DIRECT,


    /**
     * 主题交换机
     * <p>
     * 根据routing-key模糊匹配队列，*匹配任意一个字符，#匹配0个或多个字符
     */
    TOPIC,


    /**
     * 扇形交换机
     * <p>
     * 直接分发给所有绑定的队列，忽略routing-key,用于广播消息
     */
    FANOUT,


    /**
     * 头交换机
     * <p>
     * 类似直连交换机，不同于直连交换机的路由规则建立在头属性上而不是routing-key(使用较少)
     */
    HEADERS;


}
