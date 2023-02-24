package com.hqy.foundation.common.support;

import com.hqy.foundation.common.AccountEmailTemplate;
import com.hqy.foundation.common.bind.EmailTemplateInfo;
import com.hqy.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 16:46
 */
public class DefaultAccountEmailTemplate implements AccountEmailTemplate {

    private final String DEFAULT_SUBJECT;
    private final String DEFAULT_CONTENT;

    public DefaultAccountEmailTemplate(String DEFAULT_SUBJECT, String DEFAULT_CONTENT) {
        this.DEFAULT_SUBJECT = DEFAULT_SUBJECT;
        this.DEFAULT_CONTENT = DEFAULT_CONTENT;
    }

    @Override
    public EmailTemplateInfo getAccountRegistryTemplate(String receiver, String code) {
        AssertUtil.notEmpty(receiver, "Email receiver should not be empty.");
        AssertUtil.notEmpty(code, "Email access code should not be empty.");
        String content = DEFAULT_CONTENT;

        content = StringUtils.replace(content, "{receiver}", receiver);
        content = StringUtils.replace(content, "{code}", code);
        return new EmailTemplateInfo(DEFAULT_SUBJECT, content);
    }
}
