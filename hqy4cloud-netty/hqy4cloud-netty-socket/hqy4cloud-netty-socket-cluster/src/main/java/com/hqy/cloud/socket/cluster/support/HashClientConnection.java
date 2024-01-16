package com.hqy.cloud.socket.cluster.support;

import com.hqy.cloud.socket.api.AbstractClientConnection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/12
 */
public class HashClientConnection extends AbstractClientConnection {
    private int hash;

    public HashClientConnection(String connectUrl, String authorization, String context, int hash) {
        super(connectUrl, authorization, context);
        this.hash = hash;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }
}
