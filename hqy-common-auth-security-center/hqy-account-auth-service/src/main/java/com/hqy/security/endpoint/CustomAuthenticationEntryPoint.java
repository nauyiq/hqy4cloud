package com.hqy.security.endpoint;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义认证错误异常响应
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/21 16:09
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpStatus status;
        CommonResultCode resultCode;
        if (authException instanceof BadCredentialsException) {
            status = HttpStatus.OK;
            resultCode = CommonResultCode.INVALID_ACCESS_USER;
        } else {
            status = HttpStatus.FORBIDDEN;
            resultCode = CommonResultCode.USER_DISABLED;
        }

        response.setStatus(status.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, StringConstants.APPLICATION_JSON_UTF_8);
        response.getWriter().print(JsonUtil.toJson(CommonResultCode.dataResponse(resultCode)));
        response.getWriter().flush();
    }
}
