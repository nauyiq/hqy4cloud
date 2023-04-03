package com.hqy.cloud.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 11:17
 */
public class ValidationUtil {

    private static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);

    /**
     * 邮箱的正则
     */
    private static final String EMAIL_PATTERN = "^(\\w)+([\\-\\.\\w+])*@([\\w\\-\\#])+((\\.\\w+)+)$";

    private ValidationUtil() {}

    /**
     * 判断参数邮箱是否是邮箱格式
     * @param email 邮箱
     * @return true 是合格的邮箱格式
     */
    public static boolean validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        email = email.trim();
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return pattern.matcher(email).matches();
    }


}
