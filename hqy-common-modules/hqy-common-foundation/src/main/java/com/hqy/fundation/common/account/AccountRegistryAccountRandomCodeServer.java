package com.hqy.fundation.common.account;

import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.fundation.cache.redis.key.support.DefaultKeyGenerator;
import com.hqy.fundation.common.support.RedisAccountRandomCodeServer;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 17:31
 */
public class AccountRegistryAccountRandomCodeServer extends RedisAccountRandomCodeServer {

    public AccountRegistryAccountRandomCodeServer() {
        super(new DefaultKeyGenerator(MicroServiceConstants.ACCOUNT_SERVICE, "REGISTRY"));
    }
}
