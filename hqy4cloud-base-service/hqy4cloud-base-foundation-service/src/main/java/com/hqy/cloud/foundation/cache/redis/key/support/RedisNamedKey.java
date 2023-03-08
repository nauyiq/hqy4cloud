package com.hqy.cloud.foundation.cache.redis.key.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import org.apache.commons.lang3.StringUtils;

/**
 * 支持以服务名开头和自定义前缀作为业务区分的redis key
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 16:55
 */
public class RedisNamedKey implements RedisKey {

    /**
     * 服务名
     */
    private final String projectNamed;

    /**
     * key前缀
     */
    private final String prefix;

    public RedisNamedKey() {
        this(null, null);
    }

    public RedisNamedKey(String projectNamed, String prefix) {
        this.projectNamed = projectNamed;
        this.prefix = prefix;
    }

    @Override
    public String getKey() {
        if (StringUtils.isAllEmpty(projectNamed, prefix)) {
            throw new UnsupportedOperationException("Failed execute to getKey, projectNamed and prefix all empty.");
        }
        if (StringUtils.isNotBlank(projectNamed) && StringUtils.isBlank(prefix)) {
            return projectNamed;
        }
        if (StringUtils.isBlank(projectNamed) && StringUtils.isNotBlank(prefix)) {
            return prefix;
        }

        return projectNamed.concat(StrUtil.COLON).concat(prefix);
    }

}
