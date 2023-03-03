package com.hqy.foundation.util;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.util.AssertUtil;

/**
 * SocketHashFactorUtils.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/25 9:47
 */
public class SocketHashFactorUtils {


    public static String genHashFactor(String ip, int port) {
        AssertUtil.notNull(ip, "Ip should not be empty.");
        return ip.concat(StringConstants.Symbol.COLON) + port;
    }




}
