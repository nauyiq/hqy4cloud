package com.hqy.wallet.dao;

import com.hqy.base.BaseDao;
import com.hqy.common.entity.account.Wallet;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/23 15:00
 */
@Repository
public interface WalletDao extends BaseDao<Wallet, Long> {
}
