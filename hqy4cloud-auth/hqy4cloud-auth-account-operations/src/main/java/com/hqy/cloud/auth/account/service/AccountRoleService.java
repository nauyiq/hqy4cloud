package com.hqy.cloud.auth.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountRole;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12
 */
public interface AccountRoleService extends IService<AccountRole> {

    /**
     * 生成注册账号角色中间表的实体
     * @param account 账号实体
     * @return        账号角色中间表实体
     */
    List<AccountRole> registerAccountRole(Account account);

    /**
     * 更新角色level
     * @param roleId  角色id
     * @param level   角色level
     * @return        result.
     */
    boolean updateRoleLevel(Integer roleId, Integer level);

    /**
     * 批量删除表account_role数据， 其实就是删除某个角色的所有account_role数据.
     * @param roles 角色列表
     * @return      是否删除成功.
     */
    boolean deleteByAccountRoleIds(List<Integer> roles);

}
