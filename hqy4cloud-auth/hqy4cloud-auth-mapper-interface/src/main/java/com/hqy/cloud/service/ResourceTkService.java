package com.hqy.cloud.service;

import com.hqy.cloud.common.vo.AdminResourceVO;
import com.hqy.cloud.entity.Resource;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.tk.BaseTkService;

/**
 * ResourceTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:59
 */
public interface ResourceTkService extends BaseTkService<Resource, Integer> {


    /**
     * 分页获取资源列表
     * @param name    模糊查询name
     * @param current 当前页
     * @param size    页数
     * @return        PageResult.
     */
    PageResult<AdminResourceVO> getPageResources(String name, Integer current, Integer size);

}
