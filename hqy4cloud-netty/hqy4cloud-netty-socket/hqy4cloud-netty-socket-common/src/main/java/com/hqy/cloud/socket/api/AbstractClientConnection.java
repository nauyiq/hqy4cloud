package com.hqy.cloud.socket.api;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/12
 */
public abstract class AbstractClientConnection implements ClientConnection {

    private String connectUrl;
    private String authorization;
    private String context;

    public AbstractClientConnection(String connectUrl, String authorization, String context) {
        this.connectUrl = connectUrl;
        this.authorization = authorization;
        this.context = context;
    }

    @Override
    public String getConnectUrl() {
        return connectUrl;
    }

    public void setConnectUrl(String connectUrl) {
        this.connectUrl = connectUrl;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }

    @Override
    public String getContextPath() {
        return context;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }


    public void setContext(String context) {
        this.context = context;
    }
}
