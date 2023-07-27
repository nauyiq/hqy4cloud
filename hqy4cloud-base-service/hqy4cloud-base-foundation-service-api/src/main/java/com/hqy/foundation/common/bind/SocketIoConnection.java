package com.hqy.foundation.common.bind;

import lombok.AllArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 10:08
 */
@AllArgsConstructor
public class SocketIoConnection {

    private String connectUrl;

    private String authorization;

    private String context;

    private String host;


    public SocketIoConnection() {
    }


    public SocketIoConnection(String connectUrl, String authorization, String context) {
        this.connectUrl = connectUrl;
        this.authorization = authorization;
        this.context = context;
    }

    public String getConnectUrl() {
        return connectUrl;
    }

    public void setConnectUrl(String connectUrl) {
        this.connectUrl = connectUrl;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
