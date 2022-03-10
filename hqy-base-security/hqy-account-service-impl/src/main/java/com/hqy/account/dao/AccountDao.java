package com.hqy.account.dao;

import com.hqy.account.entity.Account;
import com.hqy.base.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
@Repository
public interface AccountDao extends BaseDao<Account, Long> {
}
