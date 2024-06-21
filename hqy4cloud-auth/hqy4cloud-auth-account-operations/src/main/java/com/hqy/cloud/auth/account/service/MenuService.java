package com.hqy.cloud.auth.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.account.entity.Menu;

import java.util.List;

/**
 * MenuTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:15
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取目录菜单
     * @param firstMenuParentId parent_id
     * @return                  AdminMenuInfoVO.
     */
    List<AdminMenuInfoVO> getAdminMenuInfoByParentId(long firstMenuParentId);

}
