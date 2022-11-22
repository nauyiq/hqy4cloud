package com.hqy.fundation.cache.redis.key;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * AbstractKeyGenerator.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 15:23
 */
public abstract class AbstractKeyGenerator {
    private final String project;
    private String defaultPrefix;


    public AbstractKeyGenerator(String project) {
        this(project, StringConstants.EMPTY);
    }

    public AbstractKeyGenerator(String project, String defaultPrefix) {
        this.project = project.toUpperCase();
        this.defaultPrefix = defaultPrefix.toUpperCase();
    }

    public String genPrefix() {
        AssertUtil.notEmpty(defaultPrefix, "Default prefix should not be empty.");
        return genPrefix(defaultPrefix);
    }

    public String genPrefix(String prefix) {
        return project.concat(StringConstants.Symbol.COLON).concat(prefix).concat(StringConstants.Symbol.COLON);
    }

    public String genKey(String key) {
        AssertUtil.notEmpty(defaultPrefix, "Default prefix should not be empty.");
        return genKey(defaultPrefix, key);
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

    public void setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }
}
