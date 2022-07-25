package com.hqy.common.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.common.transaction.GlobalTransactionalThriftMethod;

/**
 * 钱包服务 -> thrift rpc接口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:40
 */
@ThriftService(MicroServiceConstants.DEMO_WALLET_SERVICE)
public interface WalletRemoteService extends RPCService {

    /**
     * 修改account
     * @param account account json
     * @return 是否修改成功
     */
    @ThriftMethod
    @GlobalTransactionalThriftMethod
    boolean modifyAccount(@ThriftField(1)String account);


    /**
     * tcc更新账单信息
     * @param beforeAccount
     * @param afterAccount
     * @return
     */
    @ThriftMethod
    @GlobalTransactionalThriftMethod
    boolean tccModifyAccount(@ThriftField(1) String beforeAccount, @ThriftField(2) String afterAccount);

    /**
     * 获取钱包信息, 余额等.
     * @param id 账号id
     * @return   Wallet Json String.
     */
    @ThriftMethod
    String walletInfo(@ThriftField(1) Long id);
}
