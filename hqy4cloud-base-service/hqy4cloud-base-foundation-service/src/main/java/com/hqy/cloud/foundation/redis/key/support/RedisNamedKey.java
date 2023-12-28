package com.hqy.cloud.foundation.redis.key.support;

import com.hqy.cloud.foundation.redis.key.RedisKey;
import org.apache.commons.lang3.StringUtils;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.UNION;

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
    private final String projectName;

    /**
     * key前缀
     */
    private final String prefix;

    public RedisNamedKey(String projectName, String prefix) {
        if (StringUtils.isAllBlank(projectName, prefix)) {
            throw new UnsupportedOperationException("Failed execute to getKey, projectNamed and prefix all empty.");
        }
        this.projectName = projectName;
        this.prefix = prefix;
    }

    @Override
    public String getKey() {
        if (StringUtils.isNotBlank(projectName) && StringUtils.isBlank(prefix)) {
            return projectName;
        }
        if (StringUtils.isBlank(projectName) && StringUtils.isNotBlank(prefix)) {
            return prefix;
        }
        return projectName + UNION + prefix;
    }

}
