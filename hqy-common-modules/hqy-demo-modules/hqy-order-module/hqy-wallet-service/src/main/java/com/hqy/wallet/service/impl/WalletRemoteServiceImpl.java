package com.hqy.wallet.service.impl;

import com.hqy.wallet.service.WalletTkService;
import com.hqy.wallet.service.TccWalletService;
import com.hqy.common.entity.account.Wallet;
import com.hqy.common.service.WalletRemoteService;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:45
 */
@Service
@Slf4j
public class WalletRemoteServiceImpl extends AbstractRPCService implements WalletRemoteService {

    @Resource
    private WalletTkService walletTkService;
    @Resource
    private TccWalletService tccWalletService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyAccount(String account) {

        log.info("@@@ tcc 修改账号余额, xid:{}", RootContext.getXID());
        Wallet bean = JsonUtil.toBean(account, Wallet.class);
        if (bean == null) {
            return false;
        }
        //TODO 同理正常应该加乐观锁或者悲观锁
        return walletTkService.update(bean);
    }

    @Override
    public String walletInfo(Long account) {
        Wallet data = walletTkService.queryById(account);
        return data == null ? "" : JsonUtil.toJson(data);
    }

    @Override
    public boolean tccModifyAccount(String beforeAccount, String afterAccount) {
        return tccWalletService.modifyAccount(JsonUtil.toBean(beforeAccount, Wallet.class), JsonUtil.toBean(afterAccount, Wallet.class));
    }
}
