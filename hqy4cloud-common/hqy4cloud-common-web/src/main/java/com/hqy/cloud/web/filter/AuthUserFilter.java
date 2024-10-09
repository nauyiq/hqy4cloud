package com.hqy.cloud.web.filter;

import com.hqy.cloud.auth.utils.AuthUtils;
import jakarta.servlet.*;

import java.io.IOException;

/**
 * 将认证用户注入到上下中
 * @author hongqy
 * @date 2024/10/9
 */
public class AuthUserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 移除上下文认证用户
            AuthUtils.removeUser();
        }
    }
}
