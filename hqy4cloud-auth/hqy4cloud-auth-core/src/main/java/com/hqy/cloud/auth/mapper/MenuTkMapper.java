package com.hqy.cloud.auth.mapper;

import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.entity.Menu;
import com.hqy.cloud.auth.entity.Resource;
import com.hqy.cloud.tk.BaseTkMapper;
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
public interface MenuTkMapper extends BaseTkMapper<Menu, Long> {

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
