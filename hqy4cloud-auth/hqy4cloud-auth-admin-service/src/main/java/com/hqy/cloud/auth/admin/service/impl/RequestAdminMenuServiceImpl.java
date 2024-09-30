package com.hqy.cloud.auth.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hqy.cloud.auth.account.entity.AccountMenu;
import com.hqy.cloud.auth.account.entity.Menu;
import com.hqy.cloud.auth.account.service.AccountMenuService;
import com.hqy.cloud.auth.account.service.AccountDomainService;
import com.hqy.cloud.auth.account.service.MenuService;
import com.hqy.cloud.auth.admin.service.RequestAdminMenuService;
import com.hqy.cloud.auth.base.converter.MenuConverter;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.MenuDTO;
import com.hqy.cloud.account.response.AccountResultCode;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.auth.base.vo.BaseMenuVO;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.hqy.cloud.auth.base.AccountConstants.FIRST_MENU_PARENT_ID;
import static com.hqy.cloud.common.result.ResultCode.NOT_FOUND_MENU;


/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminMenuServiceImpl implements RequestAdminMenuService {

    private final AccountDomainService accountDomainService;
    private final MenuService menuService;
    private final AccountMenuService accountMenuService;
    private final AuthOperationService operationService;
    private final AccountOperationService accountOperationService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public R<List<AdminMenuInfoVO>> getAdminMenu(Long id) {
        List<AdminMenuInfoVO> adminMenuInfo = operationService.getAdminMenuInfo(id);
        adminMenuInfo.sort(Comparator.comparingInt(BaseMenuVO::getSortOrder));
        return R.ok(adminMenuInfo);
    }


    @Override
    public R<Boolean> addMenu(MenuDTO menuDTO) {
        AssertUtil.notNull(menuDTO, "MenuDTO should not be null.");
        Long parentId = menuDTO.getParentId();
        if (parentId != null && !parentId.equals(FIRST_MENU_PARENT_ID)) {
            Menu menu = menuService.getById(parentId);
            if (Objects.isNull(menu)) {
                return R.failed(NOT_FOUND_MENU);
            }
        }
        Menu menu = MenuConverter.CONVERTER.convert(menuDTO);
        return menuService.save(menu) ? R.ok() : R.failed();
    }

    @Override
    public R<AdminTreeMenuVO> getMenuById(Long menuId) {
        AssertUtil.notNull(menuId, "MenuId should not be null.");
        Menu menu = menuService.getById(menuId);
        if (Objects.isNull(menu)) {
            return R.failed(NOT_FOUND_MENU);
        }
        return R.ok(MenuConverter.CONVERTER.convert(menu));
    }

    @Override
    public R<Boolean> editMenu(MenuDTO menuDTO) {
        AssertUtil.notNull(menuDTO, "MenuDTO should not be null.");
        Long id = menuDTO.getId();
        Menu menu = menuService.getById(id);
        if (Objects.isNull(menu)) {
            return R.failed(NOT_FOUND_MENU);
        }
        boolean updateAccountMenu = !menuDTO.getPermission().equals(menu.getPermission());
        menu.setStatus(menuDTO.getStatus().equals(1));
        menu.setName(menuDTO.getName());
        menu.setType(menuDTO.getMenuType());
        menu.setPath(menuDTO.getPath());
        menu.setPermission(menuDTO.getPermission());
        menu.setIcon(menuDTO.getIcon());
        menu.setParentId(menuDTO.getParentId());
        menu.setSortOrder(menuDTO.getSortOrder());

        Boolean execute = transactionTemplate.execute(status -> {
            try {
                if (updateAccountMenu) {
                    UpdateWrapper<AccountMenu> wrapper = Wrappers.update();
                    wrapper.set("menu_permission", menuDTO.getPermission());
                    wrapper.eq("menu_id", id);
                    AssertUtil.isTrue(accountMenuService.update(wrapper), "Failed execute to update account menu, menuId " + id);
                }
                AssertUtil.isTrue(menuService.updateById(menu), "Failed execute to update menu, menuId " + id);
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        return Boolean.TRUE.equals(execute) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteMenu(Long menuId) {
        AssertUtil.notNull(menuId, "MenuId should not be null.");
        Menu menu = menuService.getById(menuId);
        if (Objects.isNull(menu)) {
            return R.failed(NOT_FOUND_MENU);
        }
        menu.setDeleted(true);
        return menuService.updateById(menu) ? R.ok() : R.failed();
    }

    @Override
    public R<List<Integer>> getMenuPermissionsIdByUserId(Long roleId) {
       /* if (Objects.isNull(role)) {
            return R.failed(NOT_FOUND_ROLE);
        }
        List<Integer> menuIds;
        List<RoleMenu> roleMenus = operationService.roleMenuService().queryList(new RoleMenu(roleId));
        if (CollectionUtils.isNotEmpty(roleMenus)) {
            menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        } else {
            menuIds = Collections.emptyList();
        }*/
        return R.ok();
    }

    @Override
    public R<List<AdminTreeMenuVO>> getAdminTreeMenu(Long id, Boolean status) {
        AccountInfoDTO accountInfo = accountDomainService.getAccountInfo(id);
        if (Objects.isNull(accountInfo)) {
            return R.failed(AccountResultCode.USER_NOT_FOUND);
        }
        List<AdminTreeMenuVO> adminTreeMenus = operationService.getAdminTreeMenu(id, status);
        return R.ok(adminTreeMenus);
    }

}
