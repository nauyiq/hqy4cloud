package com.hqy.cloud.actuator.filter.support;

import com.hqy.cloud.actuator.service.BasicAuthorizationService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.web.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 校验访问Actuator Http Basic认证
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/7 13:34
 */
@Slf4j
@RequiredArgsConstructor
public class EndpointBasicAuthorizationFilter implements Filter {
    private final BasicAuthorizationService basicAuthorizationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 判断是否开启HTTP BASIC认证
        if (CommonSwitcher.ENABLE_ACTUATOR_BASIC_AUTHORIZATION.isOff()) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (!basicAuthorizationService.isAuth(authorization)) {
            log.warn("Failed basic auth, authorization: {}, ip:{}", authorization, IpUtil.getRequestIp(req));
            ResponseUtil.out((HttpServletResponse) response, HttpStatus.UNAUTHORIZED.value(), R.failed(ResultCode.INVALID_CLIENT_OR_SECRET));
            return;
        }
        chain.doFilter(request, response);
    }
}
