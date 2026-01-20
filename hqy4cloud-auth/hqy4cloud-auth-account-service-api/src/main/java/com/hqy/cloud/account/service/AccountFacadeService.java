package com.hqy.cloud.account.service;

import com.hqy.cloud.account.request.AccountAuthRequest;
import com.hqy.cloud.account.request.AccountModifyRequest;
import com.hqy.cloud.account.request.AccountQueryParams;
import com.hqy.cloud.account.request.AuthenticateRequest;
import com.hqy.cloud.account.response.AccountInfo;
import com.hqy.cloud.account.response.TokenInfo;
import com.hqy.cloud.common.result.R;

import java.util.Collection;
import java.util.List;

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
     * 批量查找账户信息
     * @param ids id集合
     * @return    账户信息
     */
    R<List<AccountInfo>> queryList(Collection<Long> ids);

    /**
     * 注册并认证
     * <pre>
     *     注册并认证只支持SMS/EMAIL模式
     * </pre>
     * @param request 注册并认证请求参数
     * @return        Token信息
     */
    R<TokenInfo> registerAndAuthenticate(AuthenticateRequest request);

    /**
     * 实名认证
     * @param request 入参
     * @return        响应
     */
    R<AccountInfo> realNameAuth(AccountAuthRequest request);

    /**
     * 更改密码
     * @param request 入参
     * @return        响应
     */
    R<Boolean> updatePassword(AccountModifyRequest request);

}
