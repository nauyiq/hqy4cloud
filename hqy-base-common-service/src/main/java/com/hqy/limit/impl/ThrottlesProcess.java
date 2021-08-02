package com.hqy.limit.impl;

import com.hqy.limit.ThrottlesServer;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-02 10:45
 */
public class ThrottlesProcess implements ThrottlesServer {


    @Override
    public boolean isHackAccess(String paramStringOrUri) {
        return false;
    }

    @Override
    public boolean isWhiteIp(String remoteAddress) {
        return false;
    }

    @Override
    public boolean isBIBlockedIp(String remoteAddr) {
        return false;
    }

    @Override
    public boolean isManualBlockedIp(String remoteAddr) {
        return false;
    }
}
