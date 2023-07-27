package com.hqy.cloud.util.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.hqy.cloud.common.base.lang.AuthConstants.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/2 10:48
 */
@Slf4j
public class RequestUtil {

    private RequestUtil() {}

    public static HttpServletRequest currentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if (servletRequestAttributes == null) {
            log.warn("This is not Http request context.");
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    public static boolean checkAuthorization(String authorization) {
        return authorization.startsWith(JWT_PREFIX) || authorization.startsWith(JWT_UPPERCASE_PREFIX) || authorization.startsWith(JWT_BASIC_PREFIX);
    }





}
