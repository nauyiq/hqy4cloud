package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminMenuService;
import com.hqy.cloud.auth.base.dto.MenuDTO;
import com.hqy.cloud.auth.base.enums.MenuType;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.hqy.cloud.common.result.CommonResultCode.INVALID_MENU_TYPE;
import static com.hqy.cloud.common.result.CommonResultCode.NOT_FOUND_MENU;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:24
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMenuController {

    private final RequestAdminMenuService requestService;

    @GetMapping("/menu")
    public DataResponse getMenu(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.getAdminMenu(authentication.getId());
    }

    @PostMapping("/menu")
    public DataResponse addMenu(@Valid @RequestBody MenuDTO menuDTO) {
        if (MenuType.findMenuType(menuDTO.getMenuType()) == null) {
            return CommonResultCode.dataResponse(INVALID_MENU_TYPE);
        }
        return requestService.addMenu(menuDTO);
    }

    @PutMapping("/menu")
    public DataResponse editMenu(@Valid @RequestBody MenuDTO menuDTO) {
        if (MenuType.findMenuType(menuDTO.getMenuType()) == null) {
            return CommonResultCode.dataResponse(INVALID_MENU_TYPE);
        }
        if (menuDTO.getId() == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_MENU);
        }
        return requestService.editMenu(menuDTO);
    }

    @DeleteMapping("/menu/{menuId}")
    public DataResponse deleteMenu(@PathVariable("menuId") Long menuId) {
        if (menuId == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_MENU);
        }
        return requestService.deleteMenu(menuId);
    }

    @GetMapping("/menu/{menuId}")
    public DataResponse getMenuById(@PathVariable Long menuId) {
        if (menuId == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_MENU);
        }
        return requestService.getMenuById(menuId);
    }


    @GetMapping("/menu/tree/{roleId}")
    public DataResponse getMenuPermissionsIdByRoleId(@PathVariable("roleId") Integer roleId) {
        if (roleId == null) {
            return CommonResultCode.dataResponse(CommonResultCode.NOT_FOUND_ROLE);
        }
        return requestService.getMenuPermissionsIdByRoleId(roleId);
    }

    @GetMapping("/menu/tree")
    public DataResponse getTreeMenu(HttpServletRequest request, Boolean status) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.getAdminTreeMenu(authentication.getId(), status);
    }







}
