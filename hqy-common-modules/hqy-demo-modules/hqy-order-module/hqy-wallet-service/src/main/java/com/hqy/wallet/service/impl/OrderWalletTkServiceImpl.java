package com.hqy.wallet.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.common.entity.account.OrderWallet;
import com.hqy.wallet.dao.OrderWalletDao;
import com.hqy.wallet.service.OrderWalletTkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/23 15:09
 */
@Service
public class OrderWalletTkServiceImpl extends BaseTkServiceImpl<OrderWallet, Long> implements OrderWalletTkService {

    @Resource
    private OrderWalletDao dao;

    @Override
    public BaseDao<OrderWallet, Long> selectDao() {
        return dao;
    }
}
