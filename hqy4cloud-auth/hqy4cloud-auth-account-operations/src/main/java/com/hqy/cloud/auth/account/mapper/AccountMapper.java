package com.hqy.cloud.auth.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hqy.cloud.auth.account.entity.Account;
import org.apache.ibatis.annotations.Param;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 获取账号id
     * @param value 邮箱、用户名、手机号码
     * @return      账号id
     */
    Long getAccountIdByUsernameOrEmail(@Param("value") String value);

    /**
     * 根据id获取账号信息
     * @param id 账号id
     * @return   账号实体
     */
    Account findById(@Param("id") Long id);

    /**
     * 根据用户名或者邮箱查询账号信息
     * @param uniqueIndex     唯一索引
     * @return                Account
     */
    Account queryAccountByUniqueIndex(@Param("value") String uniqueIndex);



}
