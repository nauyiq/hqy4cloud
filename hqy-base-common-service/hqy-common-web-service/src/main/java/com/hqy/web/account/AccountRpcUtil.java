package com.hqy.web.account;

import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 账号rpc工具类
 * AccountRpcUtil.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:04
 */
public class AccountRpcUtil {

    /**
     * 根据id集合获取账号基本信息
     * @param ids id集合
     * @return
     */
    public static List<AccountBaseInfoStruct> getAccountBaseInfos(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        return accountRemoteService.getAccountBaseInfos(ids);
    }







}
