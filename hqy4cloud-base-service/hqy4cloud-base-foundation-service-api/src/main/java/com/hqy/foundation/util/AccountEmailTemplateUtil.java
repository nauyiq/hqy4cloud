package com.hqy.foundation.util;

import com.hqy.foundation.common.bind.EmailTemplateInfo;
import com.hqy.foundation.common.support.DefaultAccountEmailTemplate;

/**
 * AccountRegistryEmailTemplateUtil.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 16:40
 */
public class AccountEmailTemplateUtil {

    private final static String DEFAULT_REGISTRY_SUBJECT = "[HONGQY] Please verify your device";
    private final static String DEFAULT_REGISTRY_TEMPLATE = "Hey {receiver}!" + "\r\n" + "Verification code: {code}";


    private final static DefaultAccountEmailTemplate TEMPLATE =
            new DefaultAccountEmailTemplate(DEFAULT_REGISTRY_SUBJECT, DEFAULT_REGISTRY_TEMPLATE);


    public static EmailTemplateInfo getDefaultAccountRegistryEmailTemplate(String receiver, String code) {
        return TEMPLATE.getAccountRegistryTemplate(receiver, code);
    }


}
