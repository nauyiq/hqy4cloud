package com.hqy.foundation.common.bind;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 10:08
 */
public class SocketIoConnection {

    private String connectUrl;

    private String wtoken;

    private String context;

    private String host;


    public SocketIoConnection() {
    }

    public SocketIoConnection(String host, String context) {
        this.context = context;
        this.host = host;
    }

    public SocketIoConnection(String connectUrl, String wtoken, String context) {
        this.connectUrl = connectUrl;
        this.wtoken = wtoken;
        this.context = context;
    }

    public String getConnectUrl() {
        return connectUrl;
    }

    public void setConnectUrl(String connectUrl) {
        this.connectUrl = connectUrl;
    }

    public String getWtoken() {
        return wtoken;
    }

    public void setWtoken(String wtoken) {
        this.wtoken = wtoken;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
