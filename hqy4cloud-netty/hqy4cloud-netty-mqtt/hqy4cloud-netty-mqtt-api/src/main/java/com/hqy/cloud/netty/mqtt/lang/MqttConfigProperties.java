package com.hqy.cloud.netty.mqtt.lang;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/14
 */
@ConfigurationProperties(prefix = "hqy4cloud.mqtt")
public class MqttConfigProperties {

    /**
     * 生产者投递消息时 使用MQTT协议的语义， 类似KAFKA中的三个语义：1. At-least-once(消费者至少消费一次消息，但是可能会重复消费)
     *                                                     2. Exactly-once(每条消息有且仅消费一次，且不会重复消费)
     *                                                     3. At-most-once(消息可能会丢失，但是不会重复消费)
     */
    private final String MQTT_QOS = "mqtt-qos";







}
