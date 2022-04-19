package com.hqy.util;

import com.hqy.base.common.base.lang.BaseStringConstants;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 10:48
 */
public class RequestUtil {

    public static String getBizIdFromHttpRequestHeader(HttpServletRequest request) {
        Map<String, Object> map = getMapFromHttpRequestHeader(request);
        if (Objects.isNull(map)) {
            return null;
        }
        return map.getOrDefault("id", "").toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object>  getMapFromHttpRequestHeader(HttpServletRequest request) {
        String wtoken = request.getHeader(BaseStringConstants.Auth.JWT_PAYLOAD_KEY);
        if (StringUtils.isEmpty(wtoken)) {
            return null;
        }
        return (Map<String, Object>) JsonUtil.jsonToMap(wtoken);
    }

}
