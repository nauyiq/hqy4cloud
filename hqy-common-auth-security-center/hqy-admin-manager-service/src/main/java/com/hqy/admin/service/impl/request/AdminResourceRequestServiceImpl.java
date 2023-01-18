package com.hqy.admin.service.impl.request;

import com.hqy.access.auth.support.ResourceInRoleCacheServer;
import com.hqy.admin.service.AdminOperationService;
import com.hqy.admin.service.request.AdminResourceRequestService;
import com.hqy.auth.common.convert.ResourceConverter;
import com.hqy.auth.common.dto.ResourceDTO;
import com.hqy.auth.common.dto.RoleResourcesDTO;
import com.hqy.auth.common.vo.AdminResourceVO;
import com.hqy.auth.entity.Resource;
import com.hqy.auth.entity.Role;
import com.hqy.auth.entity.RoleResources;
import com.hqy.auth.service.AccountAuthService;
import com.hqy.auth.service.ResourceTkService;
import com.hqy.auth.service.RoleResourcesTkService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.result.PageResult;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.base.common.result.CommonResultCode.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminResourceRequestServiceImpl implements AdminResourceRequestService {

    private final AccountAuthService accountAuthService;
    private final ResourceInRoleCacheServer resourceInRoleCacheServer;
    private final AdminOperationService operationService;

    @Override
    public DataResponse getPageResources(String name, Integer current, Integer size) {
        PageResult<AdminResourceVO> pageResult = accountAuthService.getResourceTkService().getPageResources(name, current, size);
        return CommonResultCode.dataResponse(pageResult);
    }

    @Override
    public DataResponse getResourceTree(Integer resourceId) {
        Resource resource = accountAuthService.getResourceTkService().queryById(resourceId);
        if (resource == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }
        RoleResources roleResources = new RoleResources();
        roleResources.setResourceId(resourceId);
        List<RoleResources> resourcesList = accountAuthService.getRoleResourcesTkService().queryList(roleResources);
        List<Integer> roleIds = CollectionUtils.isEmpty(resourcesList) ? Collections.emptyList() :
                resourcesList.stream().map(RoleResources::getRoleId).collect(Collectors.toList());
        return CommonResultCode.dataResponse(roleIds);
    }

    @Override
    public DataResponse addResource(ResourceDTO resourceDTO) {
        AssertUtil.notNull(resourceDTO, "ResourceDTO should not be null.");
        Resource resource = ResourceConverter.CONVERTER.convert(resourceDTO);
        resource.setDateTime();
        if (!accountAuthService.getResourceTkService().insert(resource)) {
            return CommonResultCode.dataResponse(SYSTEM_ERROR_INSERT_FAIL);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse editResource(ResourceDTO resourceDTO) {
        AssertUtil.notNull(resourceDTO, "ResourceDTO should not be null.");
        Resource resource = accountAuthService.getResourceTkService().queryById(resourceDTO.getId());
        if (resource == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }

        ResourceConverter.CONVERTER.updateResourceByDTO(resourceDTO, resource);
        if (!accountAuthService.getResourceTkService().update(resource)) {
            return CommonResultCode.dataResponse(SYSTEM_ERROR_UPDATE_FAIL);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataResponse editRoleResources(RoleResourcesDTO roleResourcesDTO) {
        ResourceTkService resourceTkService = accountAuthService.getResourceTkService();
        RoleResourcesTkService roleResourcesTkService = accountAuthService.getRoleResourcesTkService();

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
            List<Role> roles = accountAuthService.getRoleTkService().queryByIds(pauseRoleIds);
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
        ResourceTkService resourceTkService = accountAuthService.getResourceTkService();
        RoleResourcesTkService roleResourcesTkService = accountAuthService.getRoleResourcesTkService();

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
