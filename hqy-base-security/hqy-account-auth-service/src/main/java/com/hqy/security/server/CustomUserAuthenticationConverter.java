package com.hqy.security.server;

import cn.hutool.core.bean.BeanUtil;
import com.hqy.security.core.user.SecurityUser;
import com.hqy.security.core.user.SecurityUserDetailServiceImpl;
import com.hqy.security.dto.UserJwtPayloadDTO;
import com.hqy.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义用户身份验证转换器
 * @author qiyuan.hong
 * @date 2022-03-11 23:32
 */
@Slf4j
//@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Resource
    private SecurityUserDetailServiceImpl userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        String name = authentication.getName();
        AssertUtil.notEmpty(name, "Invalid Authentication, name is empty.");

       SecurityUser securityUser = getSecurityUserDTO(authentication, name);
        if (Objects.isNull(securityUser)) {
            return null;
        }

        return userConvertToMap(securityUser);
    }

    private Map<String, ?> userConvertToMap(SecurityUser securityUser) {
        UserJwtPayloadDTO userJwtPayloadDTO = new UserJwtPayloadDTO(securityUser.getId(), securityUser.getPassword(), securityUser.getEmail(), securityUser.getUsername(), securityUser.getAuthorities());
        return BeanUtil.beanToMap(userJwtPayloadDTO);
    }

    private SecurityUser getSecurityUserDTO(Authentication authentication, String name) {
        SecurityUser securityUser = null;
        try {
            Object principal = authentication.getPrincipal();
            if (principal instanceof SecurityUser) {
                securityUser = (SecurityUser) principal;
            } else {
                //refresh_token默认不去调用userDetailService获取用户信息，这里手动去调用 得到 securityUser
                UserDetails userDetails = userDetailsService.loadUserByUsername(name);
                securityUser = (SecurityUser) userDetails;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return securityUser;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String haha = bCryptPasswordEncoder.encode("b7f5d4e072bcb46d16d38bcc1efc13a4");
        System.out.println(haha);
    }



}
