package com.hqy.web.service.account;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 账号rpc工具类
 * AccountRpcUtil.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:04
 */
public class AccountRpcUtil {


    public static AccountInfoDTO getAccountInfo(Long id) {
        if (id == null) {
            return null;
        }
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountInfoJson = accountRemoteService.getAccountInfoJson(id);
        return StringUtils.isBlank(accountInfoJson) ? null : JsonUtil.toBean(accountInfoJson, AccountInfoDTO.class);
    }




    public static AccountBaseInfoStruct getAccountBaseInfo(Long id) {
        if (id == null) {
            return null;
        }
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        return accountRemoteService.getAccountBaseInfo(id);
    }

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


    public static Map<Long, AccountBaseInfoStruct> getAccountBaseInfoMap(List<Long> ids) {
        List<AccountBaseInfoStruct> accountBaseInfos = getAccountBaseInfos(ids);
        return  accountBaseInfos.stream().collect(Collectors.toMap(AccountBaseInfoStruct::getId, e -> e, (k1, k2) -> k1));
    }







}
