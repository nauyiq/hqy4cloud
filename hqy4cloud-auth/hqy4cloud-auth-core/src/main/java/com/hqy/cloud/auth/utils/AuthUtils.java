package com.hqy.cloud.auth.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.auth.api.AuthUser;
import com.hqy.cloud.auth.api.AuthUserService;
import com.hqy.cloud.auth.api.support.DefaultAuthUser;
import com.hqy.cloud.auth.common.AuthUserHeaderConstants;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.auth.common.UsernamePasswordAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.hqy.cloud.common.base.lang.AuthConstants.*;

/**
 * 认证授权工具类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/25
 */
@Slf4j
@UtilityClass
public class AuthUtils {

    public AuthUser getCurrentUser() {
        HttpServletRequest request = WebUtils.currentRequest();
        Assert.notNull(request, "Current env not support spring mvc.");
        AuthUser authUser;
        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOn()) {
            // 从请求头获取用户信息
            try {
                Long id = Long.valueOf(Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.ID)));
                String username = Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.USERNAME));
                String email = Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.EMAIL));
                String phone = Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.PHONE));
                UserRole userRole = UserRole.valueOf(Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.ROLE)));
                String authorities = Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.AUTHORITIES));

                authUser = new DefaultAuthUser(id, username, email, phone, userRole,
                        Arrays.stream(StringUtils.tokenizeToStringArray(authorities, StringConstants.Symbol.COMMA)).toList());
            } catch (Exception cause) {
                log.error(cause.getMessage(), cause);
                throw new BizException(ResultCode.NOT_LOGIN.message, ResultCode.NOT_LOGIN.code);
            }

        } else {
            authUser = getAuthUserByService(request);
        }
       return authUser;
    }

    private static AuthUser getAuthUserByService(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasLength(authorization)) {
            throw new BizException(ResultCode.NOT_LOGIN.message, ResultCode.NOT_LOGIN.code);
        }
        AuthUserService service = SpringUtil.getBean(AuthUserService.class);
        if (service == null) {
            throw new BizException("Not found AuthUser service.", ResultCode.SYSTEM_ERROR.code);
        }
        return service.getAuthUserByToken(authorization);
    }

    public Long getCurrentUserId() {
        HttpServletRequest request = WebUtils.currentRequest();
        Assert.notNull(request, "Current env not support spring mvc.");
        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOn()) {
            return Long.valueOf(Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.ID)));
        } else {
            AuthUser user = getAuthUserByService(request);
            return user.getId();
        }
    }

    public String getCurrentUserName() {
        HttpServletRequest request = WebUtils.currentRequest();
        Assert.notNull(request, "Current env not support spring mvc.");
        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOn()) {
            return Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.USERNAME));
        } else {
            AuthUser user = getAuthUserByService(request);
            return user.getUsername();
        }
    }

    public String getCurrentUserEmail() {
        HttpServletRequest request = WebUtils.currentRequest();
        Assert.notNull(request, "Current env not support spring mvc.");
        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOn()) {
            return Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.EMAIL));
        } else {
            AuthUser user = getAuthUserByService(request);
            return user.getEmail();
        }
    }

    public String getCurrentUserPhone() {
        HttpServletRequest request = WebUtils.currentRequest();
        Assert.notNull(request, "Current env not support spring mvc.");
        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOn()) {
            return Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.PHONE));
        } else {
            AuthUser user = getAuthUserByService(request);
            return user.getPhone();
        }
    }

    public UserRole getCurrentUserRole() {
        HttpServletRequest request = WebUtils.currentRequest();
        Assert.notNull(request, "Current env not support spring mvc.");
        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOn()) {
            String role = Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.ROLE));
            return UserRole.valueOf(role);
        } else {
            AuthUser user = getAuthUserByService(request);
            return user.getUserRole();
        }
    }

    public List<String> getCurrentAuthorities() {
        HttpServletRequest request = WebUtils.currentRequest();
        Assert.notNull(request, "Current env not support spring mvc.");
        if (CommonSwitcher.ENABLE_DIFFUSE_INNER_USER_AUTH_INFO.isOn()) {
            String authorities = Base64.decodeStr(request.getHeader(AuthUserHeaderConstants.AUTHORITIES));
            return Arrays.asList(StringUtils.tokenizeToStringArray(authorities, StringConstants.Symbol.COMMA));
        } else {
            AuthUser user = getAuthUserByService(request);
            return user.authorities();
        }


    }


    public String getOAuthClientId(HttpServletRequest request) {
        //从请求路径中获取
        String clientId = request.getParameter(CLIENT_ID);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(clientId)) {
            return clientId;
        }
        //从请求头获取
        String basic = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(basic) && basic.startsWith(JWT_BASIC_PREFIX)) {
            basic = basic.replace(JWT_BASIC_PREFIX, Strings.EMPTY);
            String basicPlainText = URLDecoder.decode(basic, StandardCharsets.UTF_8);
            clientId = basicPlainText.split(StringConstants.Symbol.COLON)[0];
        }
        return clientId;
    }



    public boolean checkAuthorization(String authorization) {
        return authorization.startsWith(JWT_PREFIX) || authorization.startsWith(JWT_UPPERCASE_PREFIX) || authorization.startsWith(JWT_BASIC_PREFIX);
    }

    /**
     * 获取basic认证请求头
     * @param authorization 认证请求头
     * @return              {@link UsernamePasswordAuthentication}
     */
    public UsernamePasswordAuthentication getBasicAuthorization(String authorization) {
        if (!isBasicAuthorization(authorization)) {
            return null;
        }
        String basic = authorization.replace(JWT_BASIC_PREFIX, org.apache.commons.lang3.StringUtils.EMPTY).replace(JWT_LOWERCASE_BASIC_PREFIX, org.apache.commons.lang3.StringUtils.EMPTY);
        if (org.apache.commons.lang3.StringUtils.isBlank(basic)) {
            return null;
        }
        try {
            byte[] bytes = Base64.decode(basic);
            String decodeBasic = new String(bytes);
            String[] basics = decodeBasic.split(StrUtil.COLON);
            if (basics.length != 2) {
                return null;
            }
            return new UsernamePasswordAuthentication(basics[0], basics[1]);
        } catch (Throwable cause) {
            log.error("Failed execute to parse basic authentication: {}.", basic, cause);
            return null;
        }
    }

    /**
     * 判断认证请求头是不是basic认证请求头
     * @param authorization 认证请求头
     * @return              是否是basic认证
     */
    public boolean isBasicAuthorization(String authorization) {
        if (org.apache.commons.lang3.StringUtils.isBlank(authorization)) {
            return false;
        }
        return authorization.startsWith(JWT_BASIC_PREFIX) || authorization.startsWith(JWT_LOWERCASE_BASIC_PREFIX);
    }

    /**
     * 生成basic认证
     * @param username 用户名
     * @param password 密码
     * @return         basic认证.
     */
    public static String buildBasicAuth(String username, String password) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        String auth = Base64.encode((username.concat(StrUtil.COLON).concat(password)).getBytes(StandardCharsets.UTF_8));
        return JWT_BASIC_PREFIX + auth;
    }

}
