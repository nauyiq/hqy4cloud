package com.hqy.auth.dao;

import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.entity.Menu;
import com.hqy.auth.entity.Resource;
import com.hqy.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MenuDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:18
 */
@Repository
public interface MenuDao extends BaseDao<Menu, Long> {

    /**
     * 获取目录菜单
     * @param  parentId parentId.
     * @return AdminMenuInfoVO.
     */
    List<AdminMenuInfoVO> getAdminMenuByParentId(@Param("parentId") long parentId);

    /**
     * 通过菜单id列表查找资源列表（根据菜单permission）
     * @param menuIds 菜单id列表
     * @return        {@link Resource}
     */
    List<Resource> queryResourcesByMenuIds(@Param("ids") List<Integer> menuIds);

}
