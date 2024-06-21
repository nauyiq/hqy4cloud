package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.admin.service.RequestAdminResourceService;
import com.hqy.cloud.auth.base.converter.ResourceConverter;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleResourcesDTO;
import com.hqy.cloud.auth.core.authentication.support.AuthenticationCacheService;
import com.hqy.cloud.auth.account.entity.Resource;
import com.hqy.cloud.auth.account.entity.Role;
import com.hqy.cloud.auth.account.entity.RoleResources;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.auth.account.service.ResourceService;
import com.hqy.cloud.auth.account.service.RoleResourcesService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.result.ResultCode.NOT_FOUND_RESOURCE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminResourceServiceImpl implements RequestAdminResourceService {

    private final AccountOperationService accountOperationService;
    private final AuthenticationCacheService roleAuthenticationCacheServer;
    private final AuthOperationService authOperationService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public R<PageResult<ResourceDTO>> getPageResources(String name, Integer current, Integer size) {
        PageResult<ResourceDTO> pageResult = authOperationService.resourceTkService().getPageResources(name, current, size);
        return R.ok(pageResult);
    }

    @Override
    public R<List<Integer>> getResourceTree(Integer resourceId) {
        Resource resource = authOperationService.resourceTkService().queryById(resourceId);
        if (Objects.isNull(resource)) {
            return R.failed(NOT_FOUND_RESOURCE);
        }
        RoleResources roleResources = new RoleResources();
        roleResources.setResourceId(resourceId);
        List<RoleResources> resourcesList = authOperationService.roleResourcesTkService().queryList(roleResources);
        List<Integer> roleIds = CollectionUtils.isEmpty(resourcesList) ? Collections.emptyList() :
                resourcesList.stream().map(RoleResources::getRoleId).collect(Collectors.toList());
        return R.ok(roleIds);
    }

    @Override
    public R<Boolean> addResource(ResourceDTO resourceDTO) {
        AssertUtil.notNull(resourceDTO, "ResourceDTO should not be null.");
        Resource resource = ResourceConverter.CONVERTER.convert(resourceDTO);
        resource.setDateTime();
        return authOperationService.resourceTkService().insert(resource) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editResource(ResourceDTO resourceDTO) {
        AssertUtil.notNull(resourceDTO, "ResourceDTO should not be null.");
        Resource resource = authOperationService.resourceTkService().queryById(resourceDTO.getId());
        if (Objects.isNull(resource)) {
            return R.failed(NOT_FOUND_RESOURCE);
        }
        ResourceConverter.CONVERTER.updateResourceByDTO(resourceDTO, resource);
        if (authOperationService.resourceTkService().update(resource)) {
            // 更新数据库成功， 删除缓存
            List<String> roles = authOperationService.roleResourcesTkService().getRolesByResource(resourceDTO.getId());
            if (CollectionUtils.isNotEmpty(roles)) {
                roles.forEach(roleAuthenticationCacheServer::invalid);
            }
            return R.ok();
        }
        return R.failed();
    }

    @Override
    public R<Boolean> delResource(Integer resourceId) {
        ResourceService resourceService = authOperationService.resourceTkService();
        RoleResourcesService roleResourcesService = authOperationService.roleResourcesTkService();

        Resource resource = resourceService.queryById(resourceId);
        if (Objects.isNull(resource)) {
            return R.failed(NOT_FOUND_RESOURCE);
        }
        resource.setDeleted(true);
        List<RoleResources> roleResources = roleResourcesService.queryList(RoleResources.builder().resourceId(resourceId).build());

        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(resourceService.update(resource), "Failed execute to delete resource.");
                if (CollectionUtils.isNotEmpty(roleResources)) {
                    AssertUtil.isTrue(roleResourcesService.deleteByResourceIdAndRoleIds(resourceId, roleResources.stream().map(RoleResources::getRoleId).collect(Collectors.toList())),
                            "Failed execute to delete role resources.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        if (Boolean.TRUE.equals(result) && CollectionUtils.isNotEmpty(roleResources)) {
            for (RoleResources roleResource : roleResources) {
                roleAuthenticationCacheServer.invalid(roleResource.getRoleName());
            }
        }
        return Boolean.TRUE.equals(result) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editRoleResources(RoleResourcesDTO roleResourcesDTO) {
        ResourceService resourceService = authOperationService.resourceTkService();
        RoleResourcesService roleResourcesService = authOperationService.roleResourcesTkService();

        Integer resourceId = roleResourcesDTO.getResourceId();
        Resource resource = resourceService.queryById(resourceId);
        if (Objects.isNull(resource)) {
            return R.failed(NOT_FOUND_RESOURCE);
        }

        List<Integer> pauseRoleIds = roleResourcesDTO.pauseRoleIds();
        List<RoleResources> resourcesList = roleResourcesService.queryList(RoleResources.builder().resourceId(resourceId).build());
        List<Integer> oldRoleIds = resourcesList.stream().map(RoleResources::getRoleId).collect(Collectors.toList());
        if (resourcesList.size() == pauseRoleIds.size() &&
                pauseRoleIds.containsAll(oldRoleIds)) {
            return R.ok();
        }

        List<Role> roles = accountOperationService.getRoleTkService().queryByIds(pauseRoleIds);
        Boolean result = transactionTemplate.execute(status -> {
            try {
                if (CollectionUtils.isNotEmpty(resourcesList)) {
                    AssertUtil.isTrue(roleResourcesService.deleteByResourceIdAndRoleIds(resourceId, oldRoleIds), "Failed execute to delete role resource.");
                }
                if (CollectionUtils.isNotEmpty(pauseRoleIds)) {
                    List<RoleResources> resources = roles.stream().map(role -> new RoleResources(role.getId(), role.getName(), resourceId)).collect(Collectors.toList());
                    AssertUtil.isTrue(roleResourcesService.insertList(resources), "Failed execute to insert role resource.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        if (result && CollectionUtils.isNotEmpty(resourcesList)) {
            for (RoleResources roleResource : resourcesList) {
                roleAuthenticationCacheServer.invalid(roleResource.getRoleName());
            }
        }
        return result ? R.ok() : R.failed();
    }
}
