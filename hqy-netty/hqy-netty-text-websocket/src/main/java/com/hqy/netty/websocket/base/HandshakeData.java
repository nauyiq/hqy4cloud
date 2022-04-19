package com.hqy.netty.websocket.base;

import com.hqy.base.common.base.lang.BaseStringConstants;

import java.util.Map;
import java.util.Objects;

/**
 * 握手数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 15:35
 */
public class HandshakeData {

    /**
     * 握手参数
     */
    private Map<String, String> params;

    /**
     * ip
     */
    private String remoteIp;


    public String getUid() {
        if (Objects.isNull(params)) {
            return null;
        }
        return params.get(BaseStringConstants.UID);
    }


    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
