package com.hqy.cloud.file.domain.support;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.file.domain.DomainServer;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/8
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultDomainServer implements DomainServer {
    private final Environment environment;

    @Override
    public String getDomain(String env, String scene) {
        AssertUtil.notEmpty(scene, "Domain scene should not be empty.");
        String domainPropertyKey = getDomainPropertyKey(env, scene);
        return environment.getProperty(domainPropertyKey, Domain.getDefaultDomain(scene));
    }

    protected String getDomainPropertyKey(String env, String scene) {
        if (StringUtils.isBlank(env)) {
            return DomainConstants.DOMAIN_CONFIGURATION_PREFIX.concat(scene);
        }
        return DomainConstants.DOMAIN_CONFIGURATION_PREFIX.concat(StringConstants.Symbol.POINT).concat(scene);
    }

}
