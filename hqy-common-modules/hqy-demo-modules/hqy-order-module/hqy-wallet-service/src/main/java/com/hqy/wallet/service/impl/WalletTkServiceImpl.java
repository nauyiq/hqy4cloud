package com.hqy.wallet.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.common.entity.account.Wallet;
import com.hqy.wallet.dao.WalletDao;
import com.hqy.wallet.service.WalletTkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:43
 */
@Service
public class WalletTkServiceImpl extends BaseTkServiceImpl<Wallet, Long> implements WalletTkService {

    @Resource
    private WalletDao dao;

    @Override
    public BaseDao<Wallet, Long> selectDao() {
        return dao;
    }
}
