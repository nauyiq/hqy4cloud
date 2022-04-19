package com.hqy.account.dao;

import com.hqy.base.BaseDao;
import com.hqy.order.common.entity.Account;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:41
 */
@Repository
public interface TransactionAccountDao extends BaseDao<Account, Long> {
}
