package com.hqy.cloud.common.base.config;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 10:17
 */
public interface ConfigConstants {

    String SOCKET_MULTI_PARAM_KEY = "hash";
    String SOCKET_AUTHORIZATION_SECRET = "socket.authorization.secret";
    String SOCKET_SSL_KEYSTORE_KEY = "socket.ssl.keystore";
    String SOCKET_SSL_KEYSTORE_PASSWORD = "socket.ssl.keystore.password";
    String SOCKET_IO_PORT = "socket.port";
    String SOCKET_CONNECTION_HOST = "socket.connection.host";
    String SOCKET_ENABLE_CLUSTER = "socket.enable.cluster";
    String SOCKET_CLUSTER_NODES = "socket.cluster.nodes";
    String SOCKET_CLUSTER_HASH = "socket.cluster.hash";

    String APPLICATION_NAME = "spring.application.name";
    String NACOS_GROUP = "spring.cloud.nacos.discovery.group";

    String SPRING_BOOT_ADMIN_CLIENT_USERNAME = "spring.boot.admin.client.username";
    String SPRING_BOOT_ADMIN_CLIENT_PASSWORD = "spring.boot.admin.client.password";

    String DRUID_MONITOR_ENABLED_KEY = "spring.datasource.druid.stat-view-servlet.enabled";
    String DRUID_MONITOR_ALLOW_KEY = "spring.datasource.druid.stat-view-servlet.allow";
    String DRUID_MONITOR_USERNAME_KEY = "spring.datasource.druid.stat-view-servlet.loginUsername";
    String DRUID_MONITOR_PASSWORD_KEY = "spring.datasource.druid.stat-view-servlet.loginPassword";

    int DEFAULT_SOCKET_CLUSTER_NODES = 1;
    int DEFAULT_SOCKET_CLUSTER_HASH = 0;
    boolean DEFAULT_SOCKET_ENABLE_CLUSTER = false;

    String DISCOVERY_IP = "hqy4cloud.discovery.ip";









}
