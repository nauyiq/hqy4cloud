package com.hqy.cloud.socket.model;

import com.hqy.cloud.registry.common.metadata.MetadataService;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.socket.SocketConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public class SocketServerMetadata implements MetadataService {

    /**
     * socketIo server bind port.
     */
    private int port;

    /**
     * socketIo namespace contextPath.
     */
    private String contextPath;

    /**
     * enable cluster socketIo.
     */
    private boolean cluster;

    public SocketServerMetadata() {
    }

    public SocketServerMetadata(int port, String contextPath, boolean cluster) {
        this.port = port;
        this.contextPath = contextPath;
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "SocketIoMetadata{" +
                "port=" + port +
                ", contextPath='" + contextPath + '\'' +
                ", cluster=" + cluster +
                '}';
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    @Override
    public Map<String, String> getMetadataMap() {
        Map<String, String> metadataMap = new HashMap<>(2);
        metadataMap.put(SocketConstants.SOCKET_SERVER_DEPLOY_METADATA_KEY, JsonUtil.toJson(this));
        return metadataMap;
    }
}
