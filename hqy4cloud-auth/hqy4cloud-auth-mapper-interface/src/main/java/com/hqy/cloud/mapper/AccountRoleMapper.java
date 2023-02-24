package com.hqy.cloud.mapper;

import com.hqy.cloud.entity.AccountRole;
import com.hqy.cloud.tk.PrimaryLessTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:27
 */
@Repository
public interface AccountRoleMapper extends PrimaryLessTkMapper<AccountRole> {

    /**
     * 更新角色level
     * @param roleId    角色id
     * @param level     level
     * @return          影响的数目
     */
    Long updateRoleLevel(@Param("roleId")Integer roleId, @Param("level")Integer level);
}
