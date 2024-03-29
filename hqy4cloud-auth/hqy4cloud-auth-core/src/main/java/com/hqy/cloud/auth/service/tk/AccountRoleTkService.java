package com.hqy.cloud.auth.service.tk;

import com.hqy.cloud.auth.entity.AccountRole;
import com.hqy.cloud.db.tk.PrimaryLessTkService;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:29
 */
public interface AccountRoleTkService extends PrimaryLessTkService<AccountRole> {

    /**
     * 更新角色level
     * @param roleId  角色id
     * @param level   角色level
     * @return        result.
     */
    boolean updateRoleLevel(Integer roleId, Integer level);

    /**
     * 批量删除
     * @param accountRoles AccountRole实体列表
     * @return             result.
     */
    boolean deleteByAccountRoles(List<AccountRole> accountRoles);

}
