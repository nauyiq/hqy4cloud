package com.hqy.security.service;

import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.security.dto.OauthAccountRegistryDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 15:58
 */
public interface OauthAccountService {

    /**
     * 注册账号
     * @param account Request body
     * @return        MessageResponse
     */
    MessageResponse registry(OauthAccountRegistryDTO account);

}
