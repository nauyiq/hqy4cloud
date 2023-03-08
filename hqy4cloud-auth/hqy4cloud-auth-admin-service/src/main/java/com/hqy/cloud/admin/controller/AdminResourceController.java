package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminResourceService;
import com.hqy.cloud.auth.base.converter.ResourceConverter;
import com.hqy.cloud.auth.base.dto.MicroServiceType;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleResourcesDTO;
import com.hqy.cloud.auth.core.authentication.PreAuthentication;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.project.MicroServiceConstants.SERVICES;
import static com.hqy.cloud.common.result.CommonResultCode.ERROR_PARAM_UNDEFINED;
import static com.hqy.cloud.common.result.CommonResultCode.NOT_FOUND_RESOURCE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:36
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminResourceController {

    private final RequestAdminResourceService requestService;

    @GetMapping("/resource/page")
    public R<PageResult<ResourceDTO>> getAdminResourcePage(String name, Integer current, Integer size, HttpServletRequest servletRequest) {
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return requestService.getPageResources(name, current, size);
    }

    @GetMapping("/resource/tree/{resourceId}")
    public R<List<Integer>> getResourceTree(@PathVariable("resourceId") Integer resourceId) {
        return requestService.getResourceTree(resourceId);
    }

    @GetMapping("/resource/services")
    public R<List<MicroServiceType>> getServices() {
        return R.ok(SERVICES.stream().map(ResourceConverter.CONVERTER::convert).collect(Collectors.toList()));
    }

    @PostMapping("/resource")
    @PreAuthentication("sys_resource_add")
    public R<Boolean> addResource(@RequestBody @Valid ResourceDTO resourceDTO) {
        return requestService.addResource(resourceDTO);
    }

    @PutMapping("/resource")
    @PreAuthentication("sys_resource_edit")
    public R<Boolean> editResource(@RequestBody @Valid ResourceDTO resourceDTO) {
        if (Objects.isNull(resourceDTO.getId())) {
            return R.failed(NOT_FOUND_RESOURCE);
        }
        return requestService.editResource(resourceDTO);
    }

    @DeleteMapping("/resource/{resourceId}")
    @PreAuthentication("sys_resource_del")
    public R<Boolean> delResource(@PathVariable("resourceId") Integer resourceId) {
        return requestService.delResource(resourceId);
    }

    @PutMapping("/resource/role")
    @PreAuthentication("sys_resource_perm")
    public R<Boolean> editRoleResources(@Valid @RequestBody RoleResourcesDTO roleResourcesDTO) {
        return requestService.editRoleResources(roleResourcesDTO);
    }


}
