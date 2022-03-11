package com.hqy.account.service;

import com.hqy.account.entity.Account;
import com.hqy.auth.dto.UserInfoDTO;
import com.hqy.base.BaseTkService;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:17
 */
public interface AccountService extends BaseTkService<Account, Long> {


    /**
     * 查询用户信息
     * @param usernameOrEmail 用户名或者邮箱
     * @return UserInfoDTO
     */
    UserInfoDTO queryUserInfo(String usernameOrEmail);

}
