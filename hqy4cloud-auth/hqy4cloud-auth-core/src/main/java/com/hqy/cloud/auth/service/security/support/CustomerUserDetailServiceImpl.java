package com.hqy.cloud.auth.service.security.support;

import com.hqy.cloud.auth.core.SecurityUser;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.core.CustomerUserDetailService;
import com.hqy.cloud.auth.service.tk.AccountTkService;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 9:11
 */
@Slf4j
@RequiredArgsConstructor
public class CustomerUserDetailServiceImpl implements CustomerUserDetailService {
    private final AccountTkService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = service.queryAccountByUsernameOrEmail(username);
        if (Objects.isNull(account)) {
            throw new UsernameNotFoundException(ResultCode.USER_NOT_FOUND.message);
        }
        UserDetails userDetails = new SecurityUser(account.getId(), account.getUsername(), account.getPassword(), account.getEmail() ,account.getStatus(), AuthorityUtils
                .commaSeparatedStringToAuthorityList(account.getRoles()));
        //校验user.
        checkUserDetails(userDetails);
        return userDetails;
    }


    private void checkUserDetails(UserDetails user) {
        if (!user.isEnabled()) {
            log.warn("[{}] -> user status is false.", JsonUtil.toJson(user));
            throw new DisabledException(ResultCode.USER_DISABLED.message);
        } else if (!user.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
    }
}
