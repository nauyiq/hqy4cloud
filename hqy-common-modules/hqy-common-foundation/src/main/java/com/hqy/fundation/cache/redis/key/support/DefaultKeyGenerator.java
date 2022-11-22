package com.hqy.fundation.cache.redis.key.support;

import com.hqy.fundation.cache.redis.key.AbstractKeyGenerator;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 15:41
 */
public class DefaultKeyGenerator extends AbstractKeyGenerator {
    public DefaultKeyGenerator(String namespace) {
        super(namespace);
    }

    public DefaultKeyGenerator(String project, String defaultPrefix) {
        super(project, defaultPrefix);
    }
}
