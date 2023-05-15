package com.hqy.cloud.util.web;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 解析 Oauth2 Request
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 10:48
 */
@Slf4j
public class RequestUtil {

    private RequestUtil() {}

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
        return authorization.startsWith(StringConstants.Auth.JWT_PREFIX)
                || authorization.startsWith(StringConstants.Auth.UPPERCASE_JWT_PREFIX) || authorization.startsWith(StringConstants.Auth.BASIC_PREFIX);
    }





}
