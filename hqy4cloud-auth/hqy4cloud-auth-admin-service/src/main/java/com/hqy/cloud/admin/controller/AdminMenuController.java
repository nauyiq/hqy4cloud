package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminMenuService;
import com.hqy.cloud.auth.base.dto.MenuDTO;
import com.hqy.cloud.auth.base.enums.MenuType;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.auth.core.authentication.PreAuthentication;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

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
    public R<List<AdminMenuInfoVO>> getMenu(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.getAdminMenu(authentication.getId());
    }

    @PostMapping("/menu")
    @PreAuthentication("sys_menu_add")
    public R<Boolean> addMenu(@Valid @RequestBody MenuDTO menuDTO) {
        if (MenuType.findMenuType(menuDTO.getMenuType()) == null) {
            return R.failed(INVALID_MENU_TYPE);
        }
        return requestService.addMenu(menuDTO);
    }

    @PutMapping("/menu")
    @PreAuthentication("sys_menu_edit")
    public R<Boolean> editMenu(@Valid @RequestBody MenuDTO menuDTO) {
        if (MenuType.findMenuType(menuDTO.getMenuType()) == null) {
            return R.failed(INVALID_MENU_TYPE);
        }
        if (Objects.isNull(menuDTO.getId())) {
            return R.failed(NOT_FOUND_MENU);
        }
        return requestService.editMenu(menuDTO);
    }

    @DeleteMapping("/menu/{menuId}")
    @PreAuthentication("sys_menu_del")
    public R<Boolean> deleteMenu(@PathVariable("menuId") Long menuId) {
        if (Objects.isNull(menuId)) {
            return R.failed(NOT_FOUND_MENU);
        }
        return requestService.deleteMenu(menuId);
    }

    @GetMapping("/menu/{menuId}")
    public R<AdminTreeMenuVO> getMenuById(@PathVariable Long menuId) {
        if (Objects.isNull(menuId)) {
            return R.failed(NOT_FOUND_MENU);
        }
        return requestService.getMenuById(menuId);
    }

    @GetMapping("/menu/tree/{roleId}")
    public R<List<Integer>> getMenuPermissionsIdByRoleId(@PathVariable("roleId") Integer roleId) {
        return requestService.getMenuPermissionsIdByRoleId(roleId);
    }

    @GetMapping("/menu/tree")
    public R<List<AdminTreeMenuVO>> getTreeMenu(HttpServletRequest request, Boolean status) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.getAdminTreeMenu(authentication.getId(), status);
    }


}
