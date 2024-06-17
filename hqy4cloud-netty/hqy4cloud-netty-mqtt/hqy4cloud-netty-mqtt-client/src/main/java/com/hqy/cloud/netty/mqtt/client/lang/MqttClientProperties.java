package com.hqy.cloud.netty.mqtt.client.lang;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/14
 */
//@ConfigurationProperties("hqy4cloud.mqtt.client")
public class MqttClientProperties {

    /**
     * 连接mqtt服务器地址.
     */
    private String serverAddress = "serverAddr";

    /**
     * 消费者组
     */
    private String group = "group";

    /**
     * 消费者名字
     */
    private String name = "name";








}
