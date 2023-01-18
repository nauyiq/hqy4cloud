package com.hqy.auth.service;

import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.entity.Menu;
import com.hqy.base.BaseTkService;

import java.util.List;

/**
 * MenuTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:15
 */
public interface MenuTkService extends BaseTkService<Menu, Long> {

    /**
     * 获取目录菜单
     * @param firstMenuParentId parent_id
     * @return                  AdminMenuInfoVO.
     */
    List<AdminMenuInfoVO> getAdminMenuInfoByParentId(long firstMenuParentId);

}
