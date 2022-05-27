package com.hqy.auth.server;

import com.hqy.account.entity.Account;
import com.hqy.account.service.AccountTkService;
import com.hqy.auth.dto.SecurityUserDTO;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

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
    private AccountTkService accountTkService;

    @Resource
    private DefaultClientDetailsServiceImpl authClientDetailsService;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //如果身份为空说明没有认证 则采用httpBasic认证 httpBasic中存储了client_id和client_secret，开始认证client_id和client_secret
            ClientDetails clientDetails = authClientDetailsService.loadClientByClientId(username);
            if (Objects.nonNull(clientDetails)) {
                userDetails = new User(username, clientDetails.getClientSecret(), clientDetails.getAuthorities());
            }
        } else {
            //查库
            Account account = accountTkService.queryAccountByUsernameOrEmail(username);
            if (Objects.nonNull(account)) {
                userDetails = new SecurityUserDTO(account.getId(), account.getUsername(), account.getPassword(), account.getEmail() ,account.getStatus(), AuthorityUtils
                        .commaSeparatedStringToAuthorityList(account.getAuthorities()));
            }
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
        }
    }

}
