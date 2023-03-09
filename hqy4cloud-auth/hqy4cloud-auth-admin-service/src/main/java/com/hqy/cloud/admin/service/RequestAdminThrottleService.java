package com.hqy.cloud.admin.service;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.coll.struct.PageThrottledBlockResultStruct;

/**
 * AdminThrottleRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 15:11
 */
public interface RequestAdminThrottleService {

    /**
     * 分页获取节流历史记录
     * @param throttleBy 模糊查询-被什么节流
     * @param ip         模糊查询-ip
     * @param url        模糊查询-url
     * @param current    当前页
     * @param size       页行数
     * @return           R.
     */
    R<PageThrottledBlockResultStruct> getPageThrottledHistory(String throttleBy, String ip, String url, Integer current, Integer size);

    /**
     * 删除一条节流历史记录
     * @param id 节流封禁历史记录id
     * @return   R.
     */
    R<Boolean> deleteThrottledHistory(Long id);
}
