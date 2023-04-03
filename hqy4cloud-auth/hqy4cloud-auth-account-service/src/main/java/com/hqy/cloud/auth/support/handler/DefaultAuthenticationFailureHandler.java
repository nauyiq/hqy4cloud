package com.hqy.cloud.auth.support.handler;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.base.lang.SecurityConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.Result;
import com.hqy.cloud.common.result.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录认证失败逻辑.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 16:00
 */
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final MappingJackson2HttpMessageConverter errorHttpResponseConverter = new MappingJackson2HttpMessageConverter();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(HttpStatus.OK);

        String errorMessage;
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException authorizationException = (OAuth2AuthenticationException) exception;
            String errorCode = authorizationException.getError().getErrorCode();
            errorMessage = StrUtil.isBlank(authorizationException.getError().getDescription())
                    ? errorCode
                    : authorizationException.getError().getDescription();
            Result result = getResult(errorMessage, errorCode);
            this.errorHttpResponseConverter.write(R.failed(result), MediaType.APPLICATION_JSON, httpResponse);
            return;
        }
        else {
            errorMessage = exception.getLocalizedMessage();
        }
        this.errorHttpResponseConverter.write(R.failed(errorMessage), MediaType.APPLICATION_JSON, httpResponse);
    }

    private Result getResult(String errorMessage, String errorCode) {
        Result result  = new Result() {
            @Override
            public String getMessage() {
                return errorMessage;
            }
            @Override
            public int getCode() {
                return ResultCode.FAILED.code;
            }
        };

        if (StrUtil.isNotBlank(errorCode)) {
            if (errorCode.equals(OAuth2ErrorCodes.INVALID_REQUEST)) {
                result = ResultCode.ERROR_PARAM;
            } else if (errorCode.equals(SecurityConstants.INVALID_REQUEST_CODE)) {
                result = ResultCode.VERIFY_CODE_ERROR;
            }
        }
        return result;
    }
}
