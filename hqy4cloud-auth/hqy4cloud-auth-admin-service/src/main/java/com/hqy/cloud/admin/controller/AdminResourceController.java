package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminResourceService;
import com.hqy.cloud.auth.base.converter.ResourceConverter;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleResourcesDTO;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    public DataResponse getAdminResourcePage(String name, Integer current, Integer size, HttpServletRequest servletRequest) {
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return requestService.getPageResources(name, current, size);
    }

    @GetMapping("/resource/tree/{resourceId}")
    public DataResponse getResourceTree(@PathVariable("resourceId") Integer resourceId) {
        if (resourceId == null) {
            return CommonResultCode.dataResponse(ERROR_PARAM_UNDEFINED);
        }
        return requestService.getResourceTree(resourceId);
    }

    @GetMapping("/resource/services")
    public DataResponse getServices() {
        return CommonResultCode.dataResponse(SERVICES.stream().map(ResourceConverter.CONVERTER::convert).collect(Collectors.toList()));
    }

    @PostMapping("/resource")
    public DataResponse addResource(@RequestBody @Valid ResourceDTO resourceDTO) {
        return requestService.addResource(resourceDTO);
    }

    @PutMapping("/resource")
    public DataResponse editResource(@RequestBody @Valid ResourceDTO resourceDTO) {
        if (resourceDTO.getId() == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }
        return requestService.editResource(resourceDTO);
    }

    @DeleteMapping("/resource/{resourceId}")
    public DataResponse delResource(@PathVariable("resourceId") Integer resourceId) {
        if (resourceId == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_RESOURCE);
        }
        return requestService.delResource(resourceId);
    }

    @PutMapping("/resource/role")
    public DataResponse editRoleResources(@Valid @RequestBody RoleResourcesDTO roleResourcesDTO) {
        return requestService.editRoleResources(roleResourcesDTO);
    }


}
