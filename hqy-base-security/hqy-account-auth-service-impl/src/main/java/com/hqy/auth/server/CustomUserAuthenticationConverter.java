package com.hqy.auth.server;

import cn.hutool.core.bean.BeanUtil;
import com.hqy.auth.dto.SecurityUserDTO;
import com.hqy.auth.dto.UserJwtPayloadDTO;
import com.hqy.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义用户身份验证转换器
 * @author qiyuan.hong
 * @date 2022-03-11 23:32
 */
@Slf4j
@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Resource
    private AuthUserDetailServiceImpl userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        String name = authentication.getName();
        AssertUtil.notEmpty(name, "Invalid Authentication, name is empty.");

       SecurityUserDTO securityUser = getSecurityUserDTO(authentication, name);
        if (Objects.isNull(securityUser)) {
            return null;
        }

        return userConvertToMap(securityUser);
    }

    private Map<String, ?> userConvertToMap(SecurityUserDTO securityUser) {
        UserJwtPayloadDTO userJwtPayloadDTO = new UserJwtPayloadDTO(securityUser.getId(), securityUser.getPassword(), securityUser.getEmail(), securityUser.getUsername(), securityUser.getAuthorities());
        return BeanUtil.beanToMap(userJwtPayloadDTO);
    }

    private SecurityUserDTO getSecurityUserDTO(Authentication authentication, String name) {
        SecurityUserDTO securityUser = null;
        try {
            Object principal = authentication.getPrincipal();
            if (principal instanceof SecurityUserDTO) {
                securityUser = (SecurityUserDTO) principal;
            } else {
                //refresh_token默认不去调用userDetailService获取用户信息，这里手动去调用 得到 securityUser
                UserDetails userDetails = userDetailsService.loadUserByUsername(name);
                securityUser = (SecurityUserDTO) userDetails;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return securityUser;
    }



}
