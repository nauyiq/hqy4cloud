package com.hqy.cloud.auth.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.account.entity.Resource;
import com.hqy.cloud.common.result.PageResult;

/**
 * ResourceTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:59
 */
public interface ResourceService extends IService<Resource> {


    /**
     * 分页获取资源列表
     * @param name    模糊查询name
     * @param current 当前页
     * @param size    页数
     * @return        PageResult.
     */
    PageResult<ResourceDTO> getPageResources(String name, Integer current, Integer size);

}
