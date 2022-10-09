package com.hqy.account.service.impl.remote;

import com.hqy.account.dto.AccountBaseInfoDTO;
import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.AccountAuthService;
import com.hqy.account.service.impl.AccountBaseInfoCacheService;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 11:18
 */
@Service
@RequiredArgsConstructor
public class AccountRemoteServiceImpl extends AbstractRPCService implements AccountRemoteService {
    private static final Logger log = LoggerFactory.getLogger(AccountRemoteServiceImpl.class);

    private final AccountAuthService accountAuthService;
    private final AccountBaseInfoCacheService baseInfoCacheService;

    @Override
    public String getAccountInfoJson(Long id) {
        AccountInfoDTO accountInfo = accountAuthService.getAccountInfo(id);
        return accountInfo == null ? StringConstants.EMPTY : JsonUtil.toJson(accountInfo);
    }

    @Override
    public AccountBaseInfoStruct getAccountBaseInfo(Long id) {
        AccountBaseInfoDTO accountBaseInfoDTO = baseInfoCacheService.getCache(id);
        if (accountBaseInfoDTO == null) {
            return new AccountBaseInfoStruct();
        }
        return new AccountBaseInfoStruct(accountBaseInfoDTO);
    }

    @Override
    public List<AccountBaseInfoStruct> getAccountBaseInfos(List<Long> ids) {
        List<AccountBaseInfoDTO> caches = baseInfoCacheService.getCaches(ids);
        if (CollectionUtils.isEmpty(caches)) {
            return Collections.emptyList();
        }

        return caches.stream().map(AccountBaseInfoStruct::new).collect(Collectors.toList());
    }


}
