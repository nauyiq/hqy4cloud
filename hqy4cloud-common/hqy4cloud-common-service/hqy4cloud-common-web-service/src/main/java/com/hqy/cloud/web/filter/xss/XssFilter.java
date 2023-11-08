package com.hqy.cloud.web.filter.xss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 一些简单的安全过滤： xss
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/15 14:39
 */
@WebFilter(filterName = "xssFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(XssFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // replaceAll("[\r\n]" =》 Potential CRLF Injection for logs
        log.info("AuthFilter RequestURI :{}", req.getRequestURI().replaceAll("[\r\n]",""));
        // xss 过滤
        chain.doFilter(new XssWrapper(req), resp);
    }


}
