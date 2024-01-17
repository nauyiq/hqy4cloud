package com.hqy.cloud.netty.socketio.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "hqy4cloud.socket")
public class SocketIoServerProperties {

    private int port;
    private String context = "/socketio";
    private boolean cluster;

}
