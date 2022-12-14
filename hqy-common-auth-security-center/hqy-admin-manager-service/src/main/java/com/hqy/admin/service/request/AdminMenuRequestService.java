package com.hqy.admin.service.request;

import com.hqy.base.common.bind.DataResponse;

/**
 * AdminMenuRequestController.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:25
 */
public interface AdminMenuRequestService {

    /**
     * 获取后台菜单栏
     * @param id 用户id
     * @return response.
     */
    DataResponse getAdminMenu(Long id);

}
