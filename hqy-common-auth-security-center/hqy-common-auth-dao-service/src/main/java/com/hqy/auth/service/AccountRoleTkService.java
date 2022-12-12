package com.hqy.auth.service;

import com.hqy.auth.entity.AccountRole;
import com.hqy.base.BaseTkService;

import java.util.List;

/**
 * AccountRoleTkService.
 * @author qiyuan.hong
 * @date 2022-03-10 21:17
 */
public interface AccountRoleTkService extends BaseTkService<AccountRole, Integer> {

    /**
     * 根据角色名获取角色id
     * @param roleList 角色名列表
     * @return         角色id列表
     */
    List<Integer> selectIdByNames(List<String> roleList);
}
