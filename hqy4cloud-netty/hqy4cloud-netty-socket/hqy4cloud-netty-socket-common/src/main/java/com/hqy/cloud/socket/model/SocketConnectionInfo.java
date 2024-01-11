package com.hqy.cloud.socket.model;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.Parameters;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public class SocketConnectionInfo extends Parameters {
    private String connectUrl;
    private String authorization;
    private String context;

    public SocketConnectionInfo() {
    }

    public SocketConnectionInfo(String connectUrl, String authorization, String context, String host) {
        this.connectUrl = connectUrl;
        this.authorization = authorization;
        this.context = context;
    }

    public static SocketConnectionInfo of(String connectUrl, String authorization) {
        return of(connectUrl, authorization, StrUtil.EMPTY);
    }

    public static SocketConnectionInfo of(String connectUrl, String authorization, String context) {
        return new SocketConnectionInfo(connectUrl, authorization, context, null);
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
