package com.hqy.util;

import com.hqy.base.common.base.lang.BaseStringConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        return request.getParameter(BaseStringConstants.Auth.GRANT_TYPE_KEY);
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
        String clientId = request.getParameter(BaseStringConstants.Auth.CLIENT_ID);
        if (StringUtils.isNotEmpty(clientId)) {
            return clientId;
        }
        //从请求头获取
        String basic = request.getHeader(BaseStringConstants.Auth.AUTHORIZATION_KEY);
        if (StringUtils.isNotEmpty(basic) && basic.startsWith(BaseStringConstants.Auth.BASIC_PREFIX)) {
            basic = basic.replace(BaseStringConstants.Auth.BASIC_PREFIX, Strings.EMPTY);
            String basicPlainText = new String(Base64.getDecoder().decode(basic.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            //client:secret
            clientId = basicPlainText.split(BaseStringConstants.Symbol.COLON)[0];
        }
        return clientId;
    }


    public static Long idFromOauth2Request(HttpServletRequest request) {
        Map<String, Object> payload = requestMapFromOauth2Payload(request);
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
        return (Map<String, Object>) JsonUtil.jsonToMap(payload);
    }


    public static String requestPayloadFromOauth2Header(HttpServletRequest request) {
        if (request == null) {
            request = currentRequest();
        }
        String payload = request.getHeader(BaseStringConstants.Auth.JWT_PAYLOAD_KEY);
        if (StringUtils.isEmpty(payload)) {
            return null;
        }
        try {
            payload = URLDecoder.decode(payload, StandardCharsets.UTF_8.name());
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






}
