package com.hqy.admin.service;

import com.hqy.base.common.bind.DataResponse;

/**
 * AdminAccountRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 15:27
 */
public interface AdminAccountRequestService {

    /**
     * 获取用户信息
     * @param id  用户id
     * @return    response.
     */
    DataResponse getLoginUserInfo(Long id);
}
