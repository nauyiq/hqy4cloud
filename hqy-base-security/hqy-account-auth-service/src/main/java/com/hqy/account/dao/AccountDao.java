package com.hqy.account.dao;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.entity.Account;
import com.hqy.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
@Repository
public interface AccountDao extends BaseDao<Account, Long> {


    /**
     * 根据用户名或者邮箱查询账号信息
     * @param usernameOrEmail 用户名或邮箱
     * @return                Account
     */
    Account queryAccountByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * 根据id查找用户信息
     * @param id id
     * @return   AccountInfoDTO.
     */
    AccountInfoDTO getAccountInfo(@Param("id") Long id);
}
