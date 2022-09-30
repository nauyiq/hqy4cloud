package com.hqy.account.service;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.entity.Account;
import com.hqy.base.BaseTkService;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:17
 */
public interface AccountTkService extends BaseTkService<Account, Long> {

    /**
     * 根据用户名或者邮箱查询账号信息
     * @param usernameOrEmail 用户名或邮箱
     * @return                Account
     */
    Account queryAccountByUsernameOrEmail(String usernameOrEmail);

    /**
     * 查找用户信息
     * @param id 用户id
     * @return   AccountInfoDTO.
     */
    AccountInfoDTO getAccountInfo(Long id);
}
