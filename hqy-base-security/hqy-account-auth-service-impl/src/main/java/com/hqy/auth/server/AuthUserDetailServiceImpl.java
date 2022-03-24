package com.hqy.auth.server;

import com.hqy.account.service.AccountService;
import com.hqy.auth.dto.SecurityUserDTO;
import com.hqy.auth.dto.UserInfoDTO;
import com.hqy.base.common.result.CommonResultCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * spring security 加载用户核心数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 10:52
 */
@Service
public class AuthUserDetailServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthUserDetailServiceImpl.class);

    @Resource
    private AccountService accountService;

    @Resource
    private DefaultClientDetailsServiceImpl authClientDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //如果身份为空说明没有认证 则采用httpBasic认证
            //httpBasic中存储了client_id和client_secret，开始认证client_id和client_secret
            ClientDetails clientDetails = authClientDetailsService.loadClientByClientId(username);
            if (Objects.nonNull(clientDetails)) {
                String clientSecret = clientDetails.getClientSecret();
                return new User(username, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            } else {
                log.warn("@@@ loadUserByUsername -> clientDetails is null. username:{}", username);
            }
        }

        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException(CommonResultCode.USER_NOT_FOUND.message);
        }

        //查库
        UserInfoDTO userInfoDTO = accountService.queryUserInfo(username);
        if (Objects.isNull(userInfoDTO)) {
            throw new UsernameNotFoundException(CommonResultCode.USER_NOT_FOUND.message);
        }

        if (!userInfoDTO.getStatus()) {
            throw new DisabledException(CommonResultCode.USER_DISABLED.message);
        }
        //用户角色
        List<String> roles = userInfoDTO.getRoles();
        if (CollectionUtils.isEmpty(roles)) {
            roles = Collections.singletonList("admin");
        }
        SecurityUserDTO securityUserDTO = new SecurityUserDTO(username, userInfoDTO.getPassword(),
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        securityUserDTO.setId(userInfoDTO.getId());
        securityUserDTO.setStatus(userInfoDTO.getStatus());

        return securityUserDTO;
    }
}
