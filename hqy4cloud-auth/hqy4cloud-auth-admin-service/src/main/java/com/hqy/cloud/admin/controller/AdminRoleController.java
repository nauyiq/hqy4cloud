package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminRoleService;
import com.hqy.cloud.auth.base.dto.RoleDTO;
import com.hqy.cloud.auth.base.dto.RoleMenuDTO;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.core.authentication.PreAuthentication;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static com.hqy.cloud.common.result.ResultCode.ERROR_PARAM_UNDEFINED;

/**
 * 用户角色接口controller.
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
    public R<PageResult<AccountRoleVO>> getAdminRolePage(String roleName, String note, Integer current, Integer size, HttpServletRequest servletRequest) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(servletRequest);
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return requestService.getPageRoles(roleName, note, authentication.getId(), current, size);
    }

    @GetMapping("/roles")
    public R<List<Role>> getRoles(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.getRoles(authentication.getId());
    }

    @GetMapping("/role/checkLevel")
    public R<Boolean> checkLevel(Integer level, HttpServletRequest request) {
        if (Objects.isNull(level)) {
            R.failed(ERROR_PARAM_UNDEFINED);
        }
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.checkLevel(authentication.getId(), level);
    }

    @GetMapping("/role/check/{roleName}")
    public R<Boolean>  checkRoleNameExist(@PathVariable("roleName") String roleName) {
        if (StringUtils.isBlank(roleName)) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.checkRoleNameExist(roleName);
    }


    @PostMapping("/role")
    @PreAuthentication("sys_role_add")
    public R<Boolean> addRole(HttpServletRequest request, @Valid @RequestBody RoleDTO role) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.addRole(authentication.getId(), role);
    }

    @PutMapping("/role")
    @PreAuthentication("sys_role_edit")
    public R<Boolean> editRole(HttpServletRequest request, @Valid @RequestBody RoleDTO role) {
        if (Objects.isNull(role) || Objects.isNull(role.getId())) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.editRole(AuthenticationRequestContext.getAuthentication(request).getId(), role);
    }

    @DeleteMapping("/role/{id}")
    @PreAuthentication("sys_role_del")
    public R<Boolean> deleteRole(@PathVariable("id") Integer roleId) {
        if (Objects.isNull(roleId)) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteRole(roleId);
    }


    @PutMapping("/role/menu")
    @PreAuthentication("sys_role_perm")
    public R<Boolean> updateRoleMenuIds(@RequestBody @Valid RoleMenuDTO roleMenus) {
        return requestService.updateRoleMenus(roleMenus);
    }






}
