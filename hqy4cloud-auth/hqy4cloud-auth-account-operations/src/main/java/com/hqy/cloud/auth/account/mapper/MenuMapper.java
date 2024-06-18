package com.hqy.cloud.auth.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.account.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MenuDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:18
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 获取目录菜单
     * @param  parentId parentId.
     * @return AdminMenuInfoVO.
     */
    List<AdminMenuInfoVO> getAdminMenuByParentId(@Param("parentId") long parentId);


}
