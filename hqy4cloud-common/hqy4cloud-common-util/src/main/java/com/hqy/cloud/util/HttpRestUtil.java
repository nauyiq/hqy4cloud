package com.hqy.cloud.util;

import cn.hutool.core.net.URLEncodeUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
public class HttpRestUtil {



    public static String buildUrl(String url, String path, Map<String, String> params) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(url);
        if (StringUtils.isNotBlank(path)) {
            sbUrl.append(path);
        }

        if (MapUtils.isNotEmpty(params)) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : params.entrySet()) {
                if (!sbQuery.isEmpty()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncodeUtil.encode(query.getValue(), StandardCharsets.UTF_8));
                    }
                }
            }

            if (!sbQuery.isEmpty()) {
                sbUrl.append("?").append(sbQuery);
            }

        }
        return sbUrl.toString();
    }


}
