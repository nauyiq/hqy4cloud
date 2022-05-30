package com.hqy.security.service;

import com.hqy.security.dto.OauthAccountDTO;
import com.hqy.base.common.bind.MessageResponse;

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
    MessageResponse registry(OauthAccountDTO account);

}
