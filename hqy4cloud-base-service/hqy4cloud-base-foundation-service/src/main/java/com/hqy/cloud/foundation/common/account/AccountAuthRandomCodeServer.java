package com.hqy.cloud.foundation.common.account;

import com.hqy.cloud.foundation.redis.key.support.RedisNamedKey;
import com.hqy.cloud.foundation.common.support.RedisAccountRandomCodeServer;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 17:31
 */
public class AccountAuthRandomCodeServer extends RedisAccountRandomCodeServer {

    public AccountAuthRandomCodeServer() {
        super(new RedisNamedKey("", "AUTH_CODE"));
    }
}
