package com.hqy.cloud.actuator.filter.support;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.web.utils.IpUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 监听druid的请求，通过ip + userAgent的形式判断用户是否进行了basic认证，
 * 如果是则将basic认证传递给request
 * 配合EndpointBasicAuthorizationFilter使用，因此当前过滤器必须放在EndpointBasicAuthorizationFilter之前
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/25 17:10
 */
public class DruidMonitorAutoBasicAuthFilter implements Filter {
    private static final String DRUID_MONITOR_PREFIX = "spring.datasource.druid.stat-view-servlet.enabled";
    public static final String ID = "druid";
    public static final String PATTERN = "/druid/*";
    public static final int ORDERED = Ordered.LOWEST_PRECEDENCE - 1;
    /**
     * basic认证缓存类
     * 默认长度 128， 缓存1小时
     */
    private static final Cache<String, String> BASIC_AUTH_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .maximumSize(128).build();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // option请求放行.
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            chain.doFilter(request, response);
            return;
        }
        String uri = request.getRequestURI();
        if (CommonSwitcher.ENABLE_TRANSLATE_DRUID_BASIC_AUTH_FILTER.isOn() && uri.contains(ID)) {
            // 判断请求参数是否包含了basic auth
            String basicAuth = request.getParameter(HttpHeaders.AUTHORIZATION);
            String authKey = genBasicAuthKey(request);
            if (StringUtils.isBlank(basicAuth)) {
                // 如果不包含, 则缓存中判断是否存在basic认证 存在则传递到request attribute中
                basicAuth = BASIC_AUTH_CACHE.getIfPresent(authKey);
                if (StringUtils.isNotBlank(basicAuth)) {
                    request.setAttribute(HttpHeaders.AUTHORIZATION, basicAuth);
                }
            } else {
                BASIC_AUTH_CACHE.put(authKey, basicAuth);
            }
        }
        chain.doFilter(request, response);
    }

    private String genBasicAuthKey(HttpServletRequest request) {
        String requestIp = IpUtil.getRequestIp(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        return requestIp + StrUtil.COLON + userAgent;
    }


}
