package com.hqy.cloud.auth.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hqy.cloud.auth.account.entity.Account;

/**
 * @author qiyuan.hong
 * @date 2022-03-10
 */
public interface AccountDomainService extends IService<Account> {

    /**
     * 获取账号表实体
     * @param id 账号id
     * @return   Account表实体
     */
    Account findById(Long id);


    /**
     * 查询账户信息
     * @param uniqueIndex     唯一索引（用户名，手机号，邮箱）
     * @return                Account
     */
    Account queryAccountByUniqueIndex(String uniqueIndex);




}
