package com.hqy.admin.controller;

import com.hqy.admin.service.request.AdminResourceRequestService;
import com.hqy.auth.common.dto.ResourceDTO;
import com.hqy.auth.common.dto.RoleResourcesDTO;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.hqy.base.common.result.CommonResultCode.ERROR_PARAM_UNDEFINED;
import static com.hqy.base.common.result.CommonResultCode.NOT_FOUND_RESOURCE;

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

    private final AdminResourceRequestService requestService;

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
