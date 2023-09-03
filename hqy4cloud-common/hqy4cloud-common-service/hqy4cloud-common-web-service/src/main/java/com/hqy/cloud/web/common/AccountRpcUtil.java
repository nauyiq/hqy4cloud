package com.hqy.cloud.web.common;

import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.service.RemoteAccountService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.account.struct.AccountStruct;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.cloud.util.JsonUtil;
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

    public static AccountInfoDTO getAccount(Long id) {
        if (id == null) {
            return null;
        }
        RemoteAccountService remoteAccountService = RPCClient.getRemoteService(RemoteAccountService.class);
        String accountInfoJson = remoteAccountService.getAccountInfoJson(id);
        return StringUtils.isBlank(accountInfoJson) ? null : JsonUtil.toBean(accountInfoJson, AccountInfoDTO.class);
    }

    public static AccountStruct getAccountInfo(Long id) {
        if (id == null) {
            return null;
        }
        RemoteAccountService remoteAccountService = RPCClient.getRemoteService(RemoteAccountService.class);
        return remoteAccountService.getAccountById(id);
    }

    public static AccountProfileStruct getAccountProfile(Long id) {
        if (id == null) {
            return null;
        }
        RemoteAccountProfileService profileService = RPCClient.getRemoteService(RemoteAccountProfileService.class);
        return profileService.getAccountProfile(id);
    }

    public static List<AccountProfileStruct> getAccountProfiles(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        RemoteAccountProfileService profileService = RPCClient.getRemoteService(RemoteAccountProfileService.class);
        return profileService.getAccountProfiles(ids);
    }

    public static List<AccountStruct> getAccountInfos(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        RemoteAccountService remoteAccountService = RPCClient.getRemoteService(RemoteAccountService.class);
        return remoteAccountService.getAccountByIds(ids);
    }


    public static Map<Long, AccountStruct> getAccountInfoMap(List<Long> ids) {
        List<AccountStruct> accountBaseInfos = getAccountInfos(ids);
        return  accountBaseInfos.stream().collect(Collectors.toMap(AccountStruct::getId, e -> e, (k1, k2) -> k1));
    }

    public static Map<Long, AccountProfileStruct> getAccountProfileMap(List<Long> ids) {
        List<AccountProfileStruct> profiles = getAccountProfiles(ids);
        return profiles.stream().collect(Collectors.toMap(AccountProfileStruct::getId, e -> e, (k1, k2) -> k1));
    }




}
