package com.hqy.cloud.common.base.lang;

import java.util.regex.Pattern;

/**
 * 常见的正则Pattern常量类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/8
 */
public interface PatternConstants {

    /**
     * 域名PATTERN.
     */
    Pattern DOMAIN_PATTERN = Pattern.compile("^(?:https?:\\/\\/)?(?:[^\\@\\n]+@)?(?:www\\.)?([^\\:\\/\\n]+)");


}
