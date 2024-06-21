package com.hqy.cloud.auth.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hqy.cloud.auth.account.entity.AccountRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12
 */
@Mapper
public interface AccountRoleMapper extends BaseMapper<AccountRole> {

    /**
     * 更新角色level
     * @param roleId    角色id
     * @param level     level
     * @return          影响的数目
     */
    Long updateRoleLevel(@Param("roleId")Integer roleId, @Param("level")Integer level);
}
