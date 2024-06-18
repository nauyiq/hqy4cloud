package com.hqy.cloud.auth.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hqy.cloud.auth.account.entity.SysOauthClient;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:52
 */
public interface SysOauthClientService extends IService<SysOauthClient> {

    /**
     * 根据租户id获取租户信息
     * @param clientId 租户id
     * @return          租户信息
     */
    SysOauthClient findByClientId(String clientId);

}
