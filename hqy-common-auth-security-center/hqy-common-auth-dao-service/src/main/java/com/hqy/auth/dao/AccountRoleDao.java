package com.hqy.auth.dao;

import com.hqy.auth.entity.AccountRole;
import com.hqy.base.PrimaryLessTkDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:27
 */
@Repository
public interface AccountRoleDao extends PrimaryLessTkDao<AccountRole> {

    /**
     * 更新角色level
     * @param roleId    角色id
     * @param level     level
     * @return          影响的数目
     */
    Long updateRoleLevel(@Param("roleId")Integer roleId, @Param("level")Integer level);
}
