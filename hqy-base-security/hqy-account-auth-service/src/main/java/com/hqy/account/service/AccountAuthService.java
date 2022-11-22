package com.hqy.account.service;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.struct.ResourcesInRoleStruct;

import java.util.List;
import java.util.Set;

/**
 * Account Auth Service Crud.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
public interface AccountAuthService {


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
    AccountOauthClientTkService getAccountOauthClientTkService();

    /**
     * simple table crud for t_resource
     * @return ResourceTkService
     */
    ResourceTkService getResourceTkService();

    /**
     * simple table crud for t_authorities
     * @return AuthoritiesTkService
     */
    AuthoritiesTkService getAuthoritiesTkService();

    /**
     * simple table crud for t_account_role
     * @return AccountRoleTkService
     */
    AccountRoleTkService getAccountRoleTkService();

    /**
     * 根据角色列表获取资源
     * @param roles 资源
     * @return
     */
    List<ResourcesInRoleStruct> getResourcesByRoles(List<String> roles);
}
