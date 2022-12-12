package com.hqy.auth.dao;

import com.hqy.auth.entity.AccountRole;
import com.hqy.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AccountRoleDao.
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
@Repository
public interface AccountRoleDao extends BaseDao<AccountRole, Integer> {

    /**
     * 根据角色名获取角色id
     * @param roleList 角色名列表
     * @return         角色id列表
     */
    List<Integer> selectIdByNames(@Param("roles") List<String> roleList);
}
