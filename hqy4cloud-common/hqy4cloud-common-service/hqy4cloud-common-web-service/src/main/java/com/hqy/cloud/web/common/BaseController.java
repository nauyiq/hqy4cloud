package com.hqy.cloud.web.common;

import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * BaseController.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/9 9:51
 */
@Slf4j
public abstract class BaseController {

    public static HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public Long getAccessAccountId() {
        return getAccessAccountId(getRequest());
    }

    public Long getAccessAccountId(HttpServletRequest request) {
        try {
            return AuthenticationRequestContext.getAuthentication(request).getId();
        } catch (Throwable cause) {
            return null;
        }
    }

}
