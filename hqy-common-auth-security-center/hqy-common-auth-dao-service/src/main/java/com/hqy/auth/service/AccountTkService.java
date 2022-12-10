package com.hqy.auth.service;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.auth.entity.Account;
import com.hqy.base.BaseTkService;

import java.util.List;

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

    /**
     * 查找用户信息
     * @param ids 用户id 列表
     * @return    AccountInfoDTO Set.
     */
    List<AccountInfoDTO> getAccountInfos(List<Long> ids);
}
