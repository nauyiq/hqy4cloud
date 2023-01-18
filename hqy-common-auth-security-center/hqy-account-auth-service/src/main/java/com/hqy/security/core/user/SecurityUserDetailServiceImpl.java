package com.hqy.security.core.user;

import com.hqy.auth.entity.Account;
import com.hqy.auth.service.AccountTkService;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * spring security 加载用户核心数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 10:52
 */
@Service
@RequiredArgsConstructor
public class SecurityUserDetailServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(SecurityUserDetailServiceImpl.class);

    private final AccountTkService accountTkService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //检查入参
        checkUserExist(username);
        //获取UserDetail
        UserDetails userDetails = getUserDetails(username);
        //校验UserDetail
        checkUserDetails(userDetails);

        return userDetails;
    }

    private UserDetails getUserDetails(String username) {
        UserDetails userDetails = null;
        Account account = accountTkService.queryAccountByUsernameOrEmail(username);
        if (Objects.nonNull(account)) {
            userDetails = new SecurityUser(account.getId(), account.getUsername(), account.getPassword(), account.getEmail() ,account.getStatus(), AuthorityUtils
                    .commaSeparatedStringToAuthorityList(account.getRoles()));
        }
        return userDetails;
    }

    private void checkUserExist(String username) {
        if (StringUtils.isBlank(username)) {
            log.warn("Username is empty.");
            throw new UsernameNotFoundException(CommonResultCode.USER_NOT_FOUND.message);
        }
    }

    private void checkUserDetails(UserDetails user) {
        if (user == null) {
            log.warn("UserDetails is null.");
            throw new UsernameNotFoundException(CommonResultCode.USER_NOT_FOUND.message);
        }
        if (!user.isEnabled()) {
            log.warn("[{}] -> user status is false.", JsonUtil.toJson(user));
            throw new DisabledException(CommonResultCode.USER_DISABLED.message);
        } else if (!user.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
    }

}
