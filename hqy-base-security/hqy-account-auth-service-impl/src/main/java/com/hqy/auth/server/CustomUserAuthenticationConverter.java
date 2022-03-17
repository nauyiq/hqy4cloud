package com.hqy.auth.server;

import com.hqy.auth.dto.SecurityUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> resultMap = new HashMap<>(16);
        String name = authentication.getName();
        resultMap.put("username", name);

        SecurityUserDTO securityUser;
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
            return null;
        }

        resultMap.put("id", securityUser.getId());
        resultMap.put("status", securityUser.getStatus());

        //权限角色列表
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (CollectionUtils.isNotEmpty(authorities)) {
            resultMap.put("authorities", AuthorityUtils.authorityListToSet(authorities));
        }

        return resultMap;
    }
}
