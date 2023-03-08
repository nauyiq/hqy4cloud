package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.auth.base.converter.MenuConverter;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.admin.service.RequestAdminMenuService;
import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.MenuDTO;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.auth.entity.Menu;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.entity.RoleMenu;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.auth.base.Constants.FIRST_MENU_PARENT_ID;
import static com.hqy.cloud.common.result.CommonResultCode.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminMenuServiceImpl implements RequestAdminMenuService {

    private final AuthOperationService operationService;
    private final AccountOperationService accountOperationService;

    @Override
    public R<List<AdminMenuInfoVO>> getAdminMenu(Long id) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountTkService().getAccountInfo(id);
        if (Objects.isNull(accountInfo)) {
            return R.failed(USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA));
        List<AdminMenuInfoVO> adminMenuInfo = operationService.getAdminMenuInfo(roles);
        return R.ok(adminMenuInfo);
    }


    @Override
    public R<Boolean> addMenu(MenuDTO menuDTO) {
        AssertUtil.notNull(menuDTO, "MenuDTO should not be null.");
        Long parentId = menuDTO.getParentId();
        if (parentId != null && !parentId.equals(FIRST_MENU_PARENT_ID)) {
            Menu menu = operationService.menuTkService().queryById(parentId);
            if (Objects.isNull(menu)) {
                return R.failed(NOT_FOUND_MENU);
            }
        }
        Menu menu = MenuConverter.CONVERTER.convert(menuDTO);
        menu.setDateTime();
        return operationService.menuTkService().insert(menu) ? R.ok() : R.failed();
    }

    @Override
    public R<AdminTreeMenuVO> getMenuById(Long menuId) {
        AssertUtil.notNull(menuId, "MenuId should not be null.");
        Menu menu = operationService.menuTkService().queryById(menuId);
        if (Objects.isNull(menu)) {
            return R.failed(NOT_FOUND_MENU);
        }
        return R.ok(MenuConverter.CONVERTER.convert(menu));
    }

    @Override
    public R<Boolean> editMenu(MenuDTO menuDTO) {
        AssertUtil.notNull(menuDTO, "MenuDTO should not be null.");
        Long id = menuDTO.getId();
        Menu menu = operationService.menuTkService().queryById(id);
        if (Objects.isNull(menu)) {
            return R.failed(NOT_FOUND_MENU);
        }
        menu.setStatus(menuDTO.getStatus() == 1);
        menu.setName(menuDTO.getName());
        menu.setType(menuDTO.getMenuType());
        menu.setPath(menuDTO.getPath());
        menu.setPermission(menuDTO.getPermission());
        menu.setIcon(menuDTO.getIcon());
        menu.setParentId(menuDTO.getParentId());
        menu.setSortOrder(menuDTO.getSortOrder());
        return operationService.menuTkService().update(menu) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteMenu(Long menuId) {
        AssertUtil.notNull(menuId, "MenuId should not be null.");
        Menu menu = operationService.menuTkService().queryById(menuId);
        if (Objects.isNull(menu)) {
            return R.failed(NOT_FOUND_MENU);
        }
        menu.setDeleted(true);
        return operationService.menuTkService().update(menu) ? R.ok() : R.failed();
    }

    @Override
    public R<List<Integer>> getMenuPermissionsIdByRoleId(Integer roleId) {
        Role role = accountOperationService.getRoleTkService().queryById(roleId);
        if (Objects.isNull(role)) {
            return R.failed(NOT_FOUND_ROLE);
        }
        List<Integer> menuIds;
        List<RoleMenu> roleMenus = operationService.roleMenuService().queryList(new RoleMenu(roleId));
        if (CollectionUtils.isNotEmpty(roleMenus)) {
            menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        } else {
            menuIds = Collections.emptyList();
        }
        return R.ok(menuIds);
    }

    @Override
    public R<List<AdminTreeMenuVO>> getAdminTreeMenu(Long id, Boolean status) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountTkService().getAccountInfo(id);
        if (Objects.isNull(accountInfo)) {
            return R.failed(CommonResultCode.USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA));
        List<AdminTreeMenuVO> adminTreeMenus = operationService.getAdminTreeMenu(roles, status);
        return R.ok(adminTreeMenus);
    }
}
