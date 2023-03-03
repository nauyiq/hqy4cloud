package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.auth.server.support.ResourceInRoleCacheServer;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.admin.service.RequestAdminResourceService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.auth.base.converter.ResourceConverter;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleResourcesDTO;
import com.hqy.cloud.auth.entity.Resource;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.entity.RoleResources;
import com.hqy.cloud.auth.service.AccountAuthOperationService;
import com.hqy.cloud.auth.service.tk.ResourceTkService;
import com.hqy.cloud.auth.service.tk.RoleResourcesTkService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.result.CommonResultCode.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminResourceServiceImpl implements RequestAdminResourceService {

    private final AccountAuthOperationService accountAuthOperationService;
    private final ResourceInRoleCacheServer resourceInRoleCacheServer;
    private final AuthOperationService operationService;

    @Override
    public DataResponse getPageResources(String name, Integer current, Integer size) {
        PageResult<ResourceDTO> pageResult = accountAuthOperationService.getResourceTkService().getPageResources(name, current, size);
        return CommonResultCode.dataResponse(pageResult);
    }

    @Override
    public DataResponse getResourceTree(Integer resourceId) {
        Resource resource = accountAuthOperationService.getResourceTkService().queryById(resourceId);
        if (resource == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }
        RoleResources roleResources = new RoleResources();
        roleResources.setResourceId(resourceId);
        List<RoleResources> resourcesList = accountAuthOperationService.getRoleResourcesTkService().queryList(roleResources);
        List<Integer> roleIds = CollectionUtils.isEmpty(resourcesList) ? Collections.emptyList() :
                resourcesList.stream().map(RoleResources::getRoleId).collect(Collectors.toList());
        return CommonResultCode.dataResponse(roleIds);
    }

    @Override
    public DataResponse addResource(ResourceDTO resourceDTO) {
        AssertUtil.notNull(resourceDTO, "ResourceDTO should not be null.");
        Resource resource = ResourceConverter.CONVERTER.convert(resourceDTO);
        resource.setDateTime();
        if (!accountAuthOperationService.getResourceTkService().insert(resource)) {
            return CommonResultCode.dataResponse(SYSTEM_ERROR_INSERT_FAIL);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse editResource(ResourceDTO resourceDTO) {
        AssertUtil.notNull(resourceDTO, "ResourceDTO should not be null.");
        Resource resource = accountAuthOperationService.getResourceTkService().queryById(resourceDTO.getId());
        if (resource == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }

        ResourceConverter.CONVERTER.updateResourceByDTO(resourceDTO, resource);
        if (!accountAuthOperationService.getResourceTkService().update(resource)) {
            return CommonResultCode.dataResponse(SYSTEM_ERROR_UPDATE_FAIL);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataResponse editRoleResources(RoleResourcesDTO roleResourcesDTO) {
        ResourceTkService resourceTkService = accountAuthOperationService.getResourceTkService();
        RoleResourcesTkService roleResourcesTkService = accountAuthOperationService.getRoleResourcesTkService();

        Integer resourceId = roleResourcesDTO.getResourceId();
        Resource resource = resourceTkService.queryById(resourceId);
        if (resource == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }
        List<Integer> pauseRoleIds = roleResourcesDTO.pauseRoleIds();

        RoleResources roleResources = new RoleResources();
        roleResources.setResourceId(resourceId);
        List<RoleResources> resourcesList = roleResourcesTkService.queryList(roleResources);
        List<Integer> oldRoleIds = resourcesList.stream().map(RoleResources::getRoleId).collect(Collectors.toList());
        if (resourcesList.size() == pauseRoleIds.size() &&
                pauseRoleIds.containsAll(oldRoleIds)) {
            return CommonResultCode.dataResponse();
        }

        if (CollectionUtils.isNotEmpty(resourcesList)) {
            AssertUtil.isTrue(roleResourcesTkService.deleteByResourceIdAndRoleIds(resourceId, oldRoleIds), "Failed execute to delete role resource.");
        }

        if (CollectionUtils.isNotEmpty(pauseRoleIds)) {
            List<Role> roles = accountAuthOperationService.getRoleTkService().queryByIds(pauseRoleIds);
            AssertUtil.notEmpty(roles, NOT_FOUND_ROLE.message);
            List<RoleResources> resources = roles.stream().map(role -> new RoleResources(role.getId(), role.getName(), resourceId)).collect(Collectors.toList());
            AssertUtil.isTrue(roleResourcesTkService.insertList(resources), "Failed execute to insert role resource.");
        }

        for (RoleResources resources : resourcesList) {
            resourceInRoleCacheServer.invalid(resources.getRoleName());
        }

        return CommonResultCode.dataResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataResponse delResource(Integer resourceId) {
        ResourceTkService resourceTkService = accountAuthOperationService.getResourceTkService();
        RoleResourcesTkService roleResourcesTkService = accountAuthOperationService.getRoleResourcesTkService();

        Resource resource = resourceTkService.queryById(resourceId);
        if (resource == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }
        resource.setDeleted(true);
        AssertUtil.isTrue(resourceTkService.update(resource), "Failed execute to delete resource.");

        RoleResources queryRoleResource = new RoleResources();
        queryRoleResource.setResourceId(resourceId);
        List<RoleResources> roleResources = roleResourcesTkService.queryList(queryRoleResource);
        if (CollectionUtils.isNotEmpty(roleResources)) {
            AssertUtil.isTrue(roleResourcesTkService.deleteByResourceIdAndRoleIds(resourceId, roleResources.stream().map(RoleResources::getRoleId).collect(Collectors.toList())),
                    "Failed execute to delete role resources.");
            for (RoleResources roleResource : roleResources) {
                resourceInRoleCacheServer.invalid(roleResource.getRoleName());
            }
        }

        return CommonResultCode.dataResponse();
    }
}
