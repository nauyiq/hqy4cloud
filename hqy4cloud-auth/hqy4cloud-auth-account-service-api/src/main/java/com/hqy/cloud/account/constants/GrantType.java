package com.hqy.cloud.account.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author hongqy
 * @date 2026/1/14
 */
public enum GrantType {

    PASSWORD,

    SMS,

    EMAIL,


    ;

    public static List<GrantType> of(String grantTypes) {
        if (StringUtils.isBlank(grantTypes)) {
            return Collections.emptyList();
        }
        return Arrays.stream(StringUtils.split(grantTypes, ","))
                .map(e -> GrantType.valueOf(e.toUpperCase())).toList();
    }


}
