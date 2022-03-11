package com.hqy.auth.service.impl;

import com.hqy.account.service.AccountService;
import com.hqy.auth.dto.SecurityUserDTO;
import com.hqy.auth.dto.UserInfoDTO;
import com.hqy.fundation.common.result.CommonResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException(CommonResultCode.USER_NOT_FOUND.message);
        }
        UserInfoDTO userInfoDTO = accountService.queryUserInfo(username);
        if (Objects.isNull(userInfoDTO)) {
            throw new UsernameNotFoundException(CommonResultCode.USER_NOT_FOUND.message);
        }

        if (!userInfoDTO.getStatus()) {
            throw new DisabledException(CommonResultCode.USER_DISABLED.message);
        }
        //用户角色
        List<String> roles = userInfoDTO.getRoles();

        return new SecurityUserDTO(username, userInfoDTO.getPassword(),
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
}
