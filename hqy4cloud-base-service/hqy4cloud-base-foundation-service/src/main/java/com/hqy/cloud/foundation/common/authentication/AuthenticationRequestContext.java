package com.hqy.cloud.foundation.common.authentication;

import cn.hutool.core.net.URLDecoder;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.NotAuthenticationException;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.web.RequestUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static com.hqy.cloud.common.base.lang.AuthConstants.*;

/**
 * 从http请求中获取当前用户授权的信息.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 10:41
 */
@Slf4j
@UtilityClass
public class AuthenticationRequestContext {

    public AuthenticationInfo getAuthentication() {
        return getAuthentication(RequestUtil.currentRequest());
    }

    public AuthenticationInfo getAuthentication(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        try {
            String payload = URLDecoder.decode(request.getHeader(JWT_PAYLOAD_KEY), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(payload)) {
                throw new NotAuthenticationException(ResultCode.INVALID_AUTHORIZATION.message);
            }
            return JsonUtil.toBean(payload, AuthenticationInfo.class);
        } catch (Throwable cause) {
            log.error("Failed execute to decode authentication payload, cause: {}", cause.getMessage());
            throw new NotAuthenticationException(cause);
        }
    }

    public String getOAuthClientId(HttpServletRequest request) {
        //从请求路径中获取
        String clientId = request.getParameter(CLIENT_ID);
        if (StringUtils.isNotEmpty(clientId)) {
            return clientId;
        }
        //从请求头获取
        String basic = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotEmpty(basic) && basic.startsWith(JWT_BASIC_PREFIX)) {
            basic = basic.replace(JWT_BASIC_PREFIX, Strings.EMPTY);
            String basicPlainText = URLDecoder.decode(basic, StandardCharsets.UTF_8);
            clientId = basicPlainText.split(StringConstants.Symbol.COLON)[0];
        }
        return clientId;
    }



}
