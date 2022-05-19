package com.hqy.account.dao;

import com.hqy.account.entity.AccountProfile;
import com.hqy.base.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/19 17:13
 */
@Repository
public interface AccountProfileDao extends BaseDao<AccountProfile, Long> {
}
