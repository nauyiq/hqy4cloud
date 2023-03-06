package com.hqy.cloud.auth.service;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.AuthenticationDTO;
import com.hqy.cloud.auth.entity.Resource;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.tk.*;

import java.util.List;

/**
 * Account Auth Service Crud.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
public interface AccountAuthOperationService {

    /**
     * get account information,
     * @param id account id.
     * @return   AccountInfoDTO.
     */
    AccountInfoDTO getAccountInfo(Long id);

    /**
     * get account information,
     * @param ids account id List.
     * @return AccountInfoDTO
     */
    List<AccountInfoDTO> getAccountInfo(List<Long> ids);


    /**
     * 根据角色列表获取资源
     * @param roles 资源
     * @return      AuthenticationDTO.
     */
    List<AuthenticationDTO> getAuthoritiesResourcesByRoles(List<String> roles);

    /**
     * 删除角色
     * @param role 角色
     * @return     result.
     */
    boolean deleteRole(Role role);

    /**
     * 更新角色资源
     * @param role        角色
     * @param resourceIds 资源ids.
     * @return            result.
     */
    boolean updateRoleResources(Role role, List<Integer> resourceIds);

    /**
     * 更新角色资源
     * @param role      角色
     * @param resources 资源列表
     * @return          result.
     */
    boolean modifyRoleResources(Role role, List<Resource> resources);


    /**
     * simple table crud for t_account.
     * @return AccountTkService.
     */
    AccountTkService getAccountTkService();

    /**
     * simple table crud for t_account_profile.
     * @return AccountProfileTkService.
     */
    AccountProfileTkService getAccountProfileTkService();

    /**
     * simple table crud for t_account_oauth_client.
     * @return AccountOauthClientTkService.
     */
    SysOauthClientTkService getAccountOauthClientTkService();

    /**
     * simple table crud for t_resource
     * @return ResourceTkService
     */
    ResourceTkService getResourceTkService();

    /**
     * simple table crud for t_role
     * @return RoleTkService
     */
    RoleTkService getRoleTkService();

    /**
     * simple table crud for t_account_role
     * @return AccountRoleTkService
     */
    AccountRoleTkService getAccountRoleTkService();


    /**
     * simple table crud for t_role_resources
     * @return RoleResourcesTkService.
     */
    RoleResourcesTkService getRoleResourcesTkService();


}
