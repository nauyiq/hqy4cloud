package com.hqy.cloud.socket.model;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public class SocketServerInfo {

    private String applicationName;
    private String id;
    private SocketServerMetadata metadata;

    public SocketServerInfo() {
    }

    public SocketServerInfo(String applicationName, String id, SocketServerMetadata metadata) {
        this.applicationName = applicationName;
        this.id = id;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public SocketServerMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SocketServerMetadata metadata) {
        this.metadata = metadata;
    }
}
