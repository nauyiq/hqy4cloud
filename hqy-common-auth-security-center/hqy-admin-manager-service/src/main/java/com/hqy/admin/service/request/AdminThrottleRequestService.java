package com.hqy.admin.service.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;

/**
 * AdminThrottleRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 15:11
 */
public interface AdminThrottleRequestService {

    /**
     * 分页获取节流历史记录
     * @param throttleBy 模糊查询-被什么节流
     * @param ip         模糊查询-ip
     * @param url        模糊查询-url
     * @param current    当前页
     * @param size       页行数
     * @return           DataResponse.
     */
    DataResponse getPageThrottledHistory(String throttleBy, String ip, String url, Integer current, Integer size);

    /**
     * 删除一条节流历史记录
     * @param id 节流封禁历史记录id
     * @return   MessageResponse.
     */
    MessageResponse deleteThrottledHistory(Long id);
}
