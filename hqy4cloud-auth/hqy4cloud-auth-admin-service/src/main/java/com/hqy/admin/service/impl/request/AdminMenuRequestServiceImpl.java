package com.hqy.admin.service.impl.request;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.admin.service.AdminOperationService;
import com.hqy.admin.service.request.AdminMenuRequestService;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.convert.MenuConverter;
import com.hqy.cloud.common.dto.MenuDTO;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.vo.menu.AdminMenuInfoVO;
import com.hqy.cloud.common.vo.menu.AdminTreeMenuVo;
import com.hqy.cloud.entity.Menu;
import com.hqy.cloud.entity.Role;
import com.hqy.cloud.entity.RoleMenu;
import com.hqy.cloud.service.AccountAuthService;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.Constants.FIRST_MENU_PARENT_ID;
import static com.hqy.cloud.common.result.CommonResultCode.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMenuRequestServiceImpl implements AdminMenuRequestService {

    private final AdminOperationService operationService;
    private final AccountAuthService accountAuthService;

    @Override
    public DataResponse getAdminMenu(Long id) {
        AccountInfoDTO accountInfo = accountAuthService.getAccountTkService().getAccountInfo(id);
        if (accountInfo == null) {
            return CommonResultCode.dataResponse(CommonResultCode.USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA));
        List<AdminMenuInfoVO> adminMenuInfo = operationService.getAdminMenuInfo(roles);
        return CommonResultCode.dataResponse(CommonResultCode.SUCCESS, adminMenuInfo);
    }


    @Override
    public DataResponse addMenu(MenuDTO menuDTO) {
        AssertUtil.notNull(menuDTO, "MenuDTO should not be null.");

        Long parentId = menuDTO.getParentId();
        if (parentId != null && !parentId.equals(FIRST_MENU_PARENT_ID)) {
            Menu menu = operationService.menuTkService().queryById(parentId);
            if (menu == null) {
                return CommonResultCode.dataResponse(NOT_FOUND_MENU);
            }
        }
        Menu menu = MenuConverter.CONVERTER.convert(menuDTO);
        menu.setDateTime();
        if (!operationService.menuTkService().insert(menu)) {
            return CommonResultCode.dataResponse(SYSTEM_ERROR_INSERT_FAIL);
        }
        return CommonResultCode.dataResponse()  ;
    }

    @Override
    public DataResponse getMenuById(Long menuId) {
        AssertUtil.notNull(menuId, "MenuId should not be null.");

        Menu menu = operationService.menuTkService().queryById(menuId);
        if (menu == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_MENU);
        }
        AdminTreeMenuVo AdminTreeMenuVo = MenuConverter.CONVERTER.convert(menu);
        AdminTreeMenuVo.setVisible("1");
        return CommonResultCode.dataResponse(AdminTreeMenuVo);
    }

    @Override
    public DataResponse editMenu(MenuDTO menuDTO) {
        AssertUtil.notNull(menuDTO, "MenuDTO should not be null.");
        Long id = menuDTO.getId();
        Menu menu = operationService.menuTkService().queryById(id);
        if (menu == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_MENU);
        }
        menu.setStatus(menuDTO.getStatus() == 1);
        menu.setName(menuDTO.getName());
        menu.setType(menuDTO.getMenuType());
        menu.setPath(menuDTO.getPath());
        menu.setPermission(menuDTO.getPermission());
        menu.setIcon(menuDTO.getIcon());
        menu.setParentId(menuDTO.getParentId());
        menu.setSortOrder(menuDTO.getSortOrder());
        if (!operationService.menuTkService().update(menu)) {
            return CommonResultCode.dataResponse(SYSTEM_ERROR_UPDATE_FAIL);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataResponse deleteMenu(Long menuId) {
        AssertUtil.notNull(menuId, "MenuId should not be null.");

        Menu menu = operationService.menuTkService().queryById(menuId);
        if (menu == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_MENU);
        }

        menu.setDeleted(true);
        if (!operationService.menuTkService().update(menu)) {
            return CommonResultCode.dataResponse(SYSTEM_BUSY);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse getMenuPermissionsIdByRoleId(Integer roleId) {
        Role role = accountAuthService.getRoleTkService().queryById(roleId);
        if (role == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_ROLE);
        }

        List<Integer> menuIds;
        List<RoleMenu> roleMenus = operationService.roleMenuService().queryList(new RoleMenu(roleId));
        if (CollectionUtils.isNotEmpty(roleMenus)) {
            menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        } else {
            menuIds = Collections.emptyList();
        }
        return CommonResultCode.dataResponse(SUCCESS, menuIds);
    }

    @Override
    public DataResponse getAdminTreeMenu(Long id, Boolean status) {
        AccountInfoDTO accountInfo = accountAuthService.getAccountTkService().getAccountInfo(id);
        if (accountInfo == null) {
            return CommonResultCode.dataResponse(CommonResultCode.USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA));
        List<AdminTreeMenuVo> adminTreeMenuVos = operationService.getAdminTreeMenu(roles, status);
        return CommonResultCode.dataResponse(CommonResultCode.SUCCESS, adminTreeMenuVos);
    }
}
