package com.hqy.cloud.actuator.filter.support;

import com.hqy.cloud.account.response.AccountResultCode;
import com.hqy.cloud.actuator.service.BasicAuthorizationService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.web.utils.IpUtil;
import com.hqy.cloud.web.utils.ResponseUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

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
    public static final int ORDERED = Ordered.LOWEST_PRECEDENCE;
    public static final String[] PATTERNS = {"/actuator/*", "/druid/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 判断是否开启HTTP BASIC认证
        if (CommonSwitcher.ENABLE_ACTUATOR_BASIC_AUTHORIZATION.isOff()) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = getBasicAuth(req);
        if (!basicAuthorizationService.isAuth(authorization)) {
            log.warn("Failed basic auth, authorization: {}, ip:{}", authorization, IpUtil.getRequestIp(req));
            ResponseUtil.out((HttpServletResponse) response, HttpStatus.UNAUTHORIZED.value(), R.failed(AccountResultCode.INVALID_CLIENT_OR_SECRET));
            return;
        }
        chain.doFilter(request, response);
    }

    private String getBasicAuth(HttpServletRequest req) {
        String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            // 尝试从请求参数中获取
            authorization = req.getParameter(HttpHeaders.AUTHORIZATION);
        }
        if (StringUtils.isBlank(authorization)) {
            // 尝试从attribute中获取basic认证
            authorization = (String) req.getAttribute(HttpHeaders.AUTHORIZATION);
        }
        return authorization;
    }

}
