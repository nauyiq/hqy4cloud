package com.hqy.cloud.auth.support.core;

import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.base.enums.AccountResultCode;
import com.hqy.cloud.auth.security.api.UserDetailsServiceWrapper;
import com.hqy.cloud.auth.security.core.SecurityAuthUser;
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
public class DefaultUserDetailServiceImpl implements UserDetailsServiceWrapper {
    private final AccountService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = service.queryAccountByUsernameOrEmail(username);
        if (Objects.isNull(account)) {
            throw new UsernameNotFoundException(AccountResultCode.USER_NOT_FOUND.message);
        }
        UserDetails userDetails = new SecurityAuthUser(account.getId(), account.getUsername(), account.getPassword(),
                account.getEmail(), account.getPhone(), account.getStatus(), account.getRole(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(account.getAuthorities()));
        //校验user.
        checkUserDetails(userDetails);
        return userDetails;
    }


    private void checkUserDetails(UserDetails user) {
        if (!user.isEnabled()) {
            log.warn("[{}] -> user status is false.", JsonUtil.toJson(user));
            throw new DisabledException(AccountResultCode.USER_DISABLED.message);
        } else if (!user.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
    }
}
