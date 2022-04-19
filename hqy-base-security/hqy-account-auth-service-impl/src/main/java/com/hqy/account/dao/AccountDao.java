package com.hqy.account.dao;

import com.hqy.account.entity.Account;
import com.hqy.auth.dto.UserInfoDTO;
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
     * 查询用户信息
     * @param usernameOrEmail 用户名或者邮箱
     * @return UserInfoDTO
     */
    UserInfoDTO queryUserInfo(@Param("usernameOrEmail") String usernameOrEmail);
}
