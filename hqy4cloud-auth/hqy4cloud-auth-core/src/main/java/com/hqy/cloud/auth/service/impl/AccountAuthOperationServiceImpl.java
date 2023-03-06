package com.hqy.cloud.auth.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.AuthenticationDTO;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.entity.AccountRole;
import com.hqy.cloud.auth.entity.Resource;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.*;
import com.hqy.cloud.auth.service.tk.*;
import com.hqy.cloud.auth.utils.AvatarHostUtil;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Account Auth Service Crud.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Service
@RequiredArgsConstructor
public class AccountAuthOperationServiceImpl implements AccountAuthOperationService {
    private static final Logger log = LoggerFactory.getLogger(AccountAuthOperationServiceImpl.class);

    private final AccountTkService accountTkService;
    private final AccountProfileTkService accountProfileTkService;
    private final SysOauthClientTkService sysOauthClientTkService;
    private final RoleTkService roleTkService;
    private final ResourceTkService resourceTkService;
    private final RoleResourcesTkService roleResourcesTkService;
    private final AccountRoleTkService accountRoleTkService;

    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        AccountInfoDTO accountInfo = accountTkService.getAccountInfo(id);
        if (accountInfo == null) {
            return null;
        }
        AvatarHostUtil.settingAvatar(accountInfo);
        return accountInfo;
    }


    @Override
    public List<AccountInfoDTO> getAccountInfo(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<AccountInfoDTO> accountInfos = accountTkService.getAccountInfos(ids);
        if (CollectionUtils.isNotEmpty(accountInfos)) {
            accountInfos = accountInfos.stream().peek(AvatarHostUtil::settingAvatar).collect(Collectors.toList());
        }
        return accountInfos;
    }


    @Override
    public List<AuthenticationDTO> getAuthoritiesResourcesByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roleResourcesTkService.getAuthoritiesResourcesByRoles(roles);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Role role) {
        role.setDeleted(true);
        if (!roleTkService.update(role)) {
            return false;
        }

        // delete account_role
        List<AccountRole> accountRoles = accountRoleTkService.queryList(new AccountRole(role.getId()));
        if (CollectionUtils.isNotEmpty(accountRoles)) {
            AssertUtil.isTrue(accountRoleTkService.deleteByAccountRoles(accountRoles), "Failed execute to delete account role.");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleResources(Role role, List<Integer> resourceIds) {
        List<Resource> resources = resourceTkService.queryByIds(resourceIds);
        if (CollectionUtils.isEmpty(resources)) {
            return false;
        }
        roleResourcesTkService.insertOrUpdateRoleResources(role.getId(), role.getName(),
                resources.stream().map(resource -> new ResourceDTO(resource.getId(), resource.getPath(), resource.getMethod(), resource.getPermission())).collect(Collectors.toList()));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyRoleResources(Role role, List<Resource> resources) {
        // 获取修改之前的角色资源数据.
        List<AuthenticationDTO> authoritiesResourcesByRoles = getAuthoritiesResourcesByRoles(Collections.singletonList(role.getName()));
        if (CollectionUtils.isNotEmpty(authoritiesResourcesByRoles) && CollectionUtils.isNotEmpty(authoritiesResourcesByRoles.get(0).getResources())) {
            List<ResourceDTO> resourceStructs = authoritiesResourcesByRoles.get(0).getResources();
            // permission表示是menu表需要进行校验的资源数据.
            List<Integer> resourceIds = resourceStructs.stream().filter(e -> StringUtils.isNotBlank(e.getPermission())).map(ResourceDTO::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(resourceIds)) {
                AssertUtil.isTrue(roleResourcesTkService.deleteByRoleAndResourceIds(role.getId(), resourceIds), "Failed execute to delete role resources.");
            }
        }
        List<ResourceDTO> resourceDTOS = resources.stream().map(resource -> new ResourceDTO(resource.getId(), resource.getPath())).collect(Collectors.toList());
        return roleResourcesTkService.insertOrUpdateRoleResources(role.getId(), role.getName(), resourceDTOS);
    }

    @Override
    public AccountTkService getAccountTkService() {
        return accountTkService;
    }

    @Override
    public AccountProfileTkService getAccountProfileTkService() {
        return accountProfileTkService;
    }

    @Override
    public SysOauthClientTkService getAccountOauthClientTkService() {
        return sysOauthClientTkService;
    }

    public SysOauthClientTkService getSysOauthClientTkService() {
        return sysOauthClientTkService;
    }

    @Override
    public ResourceTkService getResourceTkService() {
        return resourceTkService;
    }

    @Override
    public RoleTkService getRoleTkService() {
        return roleTkService;
    }

    @Override
    public AccountRoleTkService getAccountRoleTkService() {
        return accountRoleTkService;
    }

    @Override
    public RoleResourcesTkService getRoleResourcesTkService() {
        return roleResourcesTkService;
    }
}
