package com.hqy.auth.dao;

import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.entity.Menu;
import com.hqy.base.PrimaryLessTkDao;
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
public interface MenuDao extends PrimaryLessTkDao<Menu> {

    /**
     * 获取目录菜单
     * @param  parentId parentId.
     * @return AdminMenuInfoVO.
     */
    List<AdminMenuInfoVO> getAdminMenuByParentId(@Param("parentId") long parentId);
}
