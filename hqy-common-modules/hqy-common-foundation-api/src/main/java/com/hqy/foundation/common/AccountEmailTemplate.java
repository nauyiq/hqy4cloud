package com.hqy.foundation.common;

import com.hqy.foundation.common.bind.EmailTemplateInfo;

/**
 * AccountEmailTemplate.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 16:41
 */
public interface AccountEmailTemplate {

    /**
     * 获取账号注册邮件模板.
     * @param receiver 接收者.
     * @param code     邮件code.
     * @return     {@link EmailTemplateInfo}
     */
    EmailTemplateInfo getAccountRegistryTemplate(String receiver, String code);

}
