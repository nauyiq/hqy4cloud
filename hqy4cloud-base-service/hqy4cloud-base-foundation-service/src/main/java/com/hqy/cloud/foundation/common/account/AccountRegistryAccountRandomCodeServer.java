package com.hqy.cloud.foundation.common.account;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.cloud.foundation.common.support.RedisAccountRandomCodeServer;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 17:31
 */
public class AccountRegistryAccountRandomCodeServer extends RedisAccountRandomCodeServer {

    public AccountRegistryAccountRandomCodeServer() {
        super(new RedisNamedKey(MicroServiceConstants.ACCOUNT_SERVICE, "REGISTRY"));
    }
}
