package com.hqy.fundation.cache.redis.key;

import com.hqy.base.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * AbstractKeyGenerator.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 15:23
 */
public abstract class AbstractKeyGenerator {
    private final String project;
    public AbstractKeyGenerator(String project) {
        this.project = project.toUpperCase();
    }

    public String genPrefix(String prefix) {
        return project.concat(StringConstants.Symbol.COLON).concat(prefix).concat(StringConstants.Symbol.COLON);
    }

    public String genKey(String prefix, String key) {
        String genKey = project.concat(StringConstants.Symbol.COLON);
        if (StringUtils.isBlank(prefix)) {
            genKey = genKey + key;
        } else {
            genKey = genKey + prefix + StringConstants.Symbol.COLON  + key;
        }
        return genKey;
    }

    public String getProject() {
        return project;
    }
}
