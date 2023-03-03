package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminRoleService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.auth.base.dto.RoleDTO;
import com.hqy.cloud.auth.base.dto.RoleMenuDTO;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.hqy.cloud.common.result.CommonResultCode.ERROR_PARAM_UNDEFINED;
import static com.hqy.cloud.common.result.CommonResultCode.USER_NOT_FOUND;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/14 15:28
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RequestAdminRoleService requestService;

    @GetMapping("/role/page")
    public DataResponse getAdminRolePage(String roleName, String note, Integer current, Integer size, HttpServletRequest servletRequest) {
        Long id = OauthRequestUtil.idFromOauth2Request(servletRequest);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return requestService.getPageRoles(roleName, note, id, current, size);
    }

    @GetMapping("/roles")
    public DataResponse getRoles(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        return requestService.getRoles(id);
    }

    @GetMapping("/role/checkLevel")
    public DataResponse checkLevel(Integer level, HttpServletRequest request) {
        if (level == null) {
            return CommonResultCode.dataResponse(ERROR_PARAM_UNDEFINED);
        }
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        return requestService.checkLevel(id, level);
    }

    @GetMapping("/role/check/{roleName}")
    public DataResponse checkRoleNameExist(@PathVariable("roleName") String roleName) {
        if (StringUtils.isBlank(roleName)) {
            return CommonResultCode.dataResponse(ERROR_PARAM_UNDEFINED);
        }
        return requestService.checkRoleNameExist(roleName);
    }


    @PostMapping("/role")
    public DataResponse addRole(HttpServletRequest request, @Valid @RequestBody RoleDTO role) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        return requestService.addRole(id, role);
    }

    @PutMapping("/role")
    public DataResponse editRole(HttpServletRequest request, @Valid @RequestBody RoleDTO role) {
        if (role.getId() == null || role.getLevel() == null) {
            return CommonResultCode.dataResponse(ERROR_PARAM_UNDEFINED);
        }
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }

        return requestService.editRole(id, role);
    }

    @DeleteMapping("/role/{id}")
    public DataResponse deleteRole(@PathVariable("id") Integer roleId) {
        if (roleId == null) {
            return CommonResultCode.dataResponse(ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteRole(roleId);
    }


    @PutMapping("/role/menu")
    public DataResponse updateRoleMenuIds(@RequestBody @Valid RoleMenuDTO roleMenus) {
        return requestService.updateRoleMenus(roleMenus);
    }






}
