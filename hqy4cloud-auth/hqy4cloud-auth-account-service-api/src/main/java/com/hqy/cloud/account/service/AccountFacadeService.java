package com.hqy.cloud.account.service;

import com.hqy.cloud.account.request.AccountQueryParams;
import com.hqy.cloud.account.request.RegistryAccountByPhoneParams;
import com.hqy.cloud.account.response.AccountInfo;
import com.hqy.cloud.account.response.RegisterInfo;
import com.hqy.cloud.common.bind.R;

/**
 * 远程调用service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
public interface AccountFacadeService {

    /**
     * 查询账号信息
     * @param queryParams 入参
     * @return            账号信息
     */
    R<AccountInfo> query(AccountQueryParams queryParams);

    /**
     * 根据手机号注册账号
     * @param registryParams 注册账号
     * @return               认证信息
     */
    R<RegisterInfo> registerByPhone(RegistryAccountByPhoneParams registryParams);

}
