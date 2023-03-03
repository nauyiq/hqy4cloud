package com.hqy.cloud.util;

import cn.hutool.core.net.URLDecoder;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 解析 Oauth2 Request
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 10:48
 */
@Slf4j
public class OauthRequestUtil {

    private OauthRequestUtil() {}


    public static String requestGrantType() {
        HttpServletRequest request = currentRequest();
        return requestGrantType(request);
    }

    /**
     * 获取当前请求的oauth2授权模式
     * @return oauth2授权模式
     */
    public static String requestGrantType(HttpServletRequest request) {
        return request.getParameter(StringConstants.Auth.GRANT_TYPE_KEY);
    }


    public static String requestOauthClientId() {
        HttpServletRequest request = currentRequest();
        return requestOauthClientId(request);
    }

    /**
     * 获取当前请求的Oauth2 client id
     * @return client id
     */
    public static String requestOauthClientId(HttpServletRequest request) {
        //从请求路径中获取
        String clientId = request.getParameter(StringConstants.Auth.CLIENT_ID);
        if (StringUtils.isNotEmpty(clientId)) {
            return clientId;
        }
        //从请求头获取
        String basic = request.getHeader(StringConstants.Auth.AUTHORIZATION_KEY);
        if (StringUtils.isNotEmpty(basic) && basic.startsWith(StringConstants.Auth.BASIC_PREFIX)) {
            basic = basic.replace(StringConstants.Auth.BASIC_PREFIX, Strings.EMPTY);
            String basicPlainText = URLDecoder.decode(basic, StandardCharsets.UTF_8);
            //client:secret
            clientId = basicPlainText.split(StringConstants.Symbol.COLON)[0];
        }
        return clientId;
    }


    public static Long idFromOauth2Request(HttpServletRequest request) {
        Map<String, Object> payload = requestMapFromOauth2Payload(request);
        if (MapUtils.isEmpty(payload)) {
            return null;
        }
        Object id = payload.get("id");
        if (id instanceof Integer) {
            return Long.parseLong(id + "");
        }
        return (Long) payload.get("id");
    }



    public static Map<String, Object> requestMapFromOauth2Payload() {
        HttpServletRequest request = currentRequest();
        return requestMapFromOauth2Payload(request);
    }


    /**
     * 获取解析jwt放到请求头中的payload并转成map
     * @return payload
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> requestMapFromOauth2Payload(HttpServletRequest request) {
        String payload = requestPayloadFromOauth2Header(request);
        if (StringUtils.isBlank(payload)) {
            return null;
        }
        return (Map<String, Object>) JsonUtil.jsonToMap(payload);
    }


    public static String requestPayloadFromOauth2Header(HttpServletRequest request) {
        if (request == null) {
            request = currentRequest();
        }
        String payload = request.getHeader(StringConstants.Auth.JWT_PAYLOAD_KEY);
        if (StringUtils.isEmpty(payload)) {
            return null;
        }
        try {
            payload = URLDecoder.decode(payload, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return payload;
    }


    /**
     * 获取当前请求中的HttpServletRequest
     * @return HttpServletRequest
     */
    public static HttpServletRequest currentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        AssertUtil.notNull(servletRequestAttributes, "Get current RequestServlet failure.");
        return servletRequestAttributes.getRequest();
    }


    public static boolean checkAuthorization(String authorization) {
        if(authorization.startsWith(StringConstants.Auth.JWT_PREFIX) || authorization.startsWith(StringConstants.Auth.UPPERCASE_JWT_PREFIX)) {
            return true;
        } else if (authorization.startsWith(StringConstants.Auth.BASIC_PREFIX)) {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("@@@ Authorization Basic client Id, Authorization = {}", authorization);
            }
        }
        return false;
    }





}
