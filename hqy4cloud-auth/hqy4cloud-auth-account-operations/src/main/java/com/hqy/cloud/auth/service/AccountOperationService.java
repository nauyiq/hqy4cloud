package com.hqy.cloud.auth.service;

import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.UserDTO;

import java.util.List;

/**
 * 账号db操作相关service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27
 */
public interface AccountOperationService {

    /**
     * 注册账号
     * @param account         account实体
     * @return                是否成功
     */
    boolean registryAccount(Account account);





}
