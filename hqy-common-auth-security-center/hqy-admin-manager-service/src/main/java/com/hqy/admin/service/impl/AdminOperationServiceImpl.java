package com.hqy.admin.service.impl;

import com.hqy.admin.service.AdminOperationService;
import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.dao.RoleMenuDao;
import com.hqy.auth.service.RoleTkService;
import com.hqy.auth.service.MenuTkService;
import com.hqy.auth.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.auth.common.Constants.FIRST_MENU_PARENT_ID;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOperationServiceImpl implements AdminOperationService {

    private final RoleTkService roleTkService;
    private final MenuTkService menuTkService;
    private final RoleMenuService roleMenuService;

    @Override
    public List<String> getManuPermissionsByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        List<Integer> ids = roleTkService.selectIdByNames(roles);
        return ((RoleMenuDao)(roleMenuService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuInfoVO> getAdminMenuInfo(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }

        List<AdminMenuInfoVO> vos = menuTkService.getAdminMenuInfoByParentId(FIRST_MENU_PARENT_ID);
        if (CollectionUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }

        List<Integer> ids = roleTkService.selectIdByNames(roles);
        List<String> permissions = ((RoleMenuDao) (roleMenuService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

        vos = setPermissions(vos, permissions);
        return vos;
    }

    private List<AdminMenuInfoVO> setPermissions(List<AdminMenuInfoVO> vos, List<String> permissions) {
        return vos.stream().peek(vo -> {
            String permission = vo.getPermission();
            if (StringUtils.isNotBlank(permission)) {
                vo.setVisible(permissions.contains(permission) ? "1" : "0");
            } else {
                vo.setVisible("1");
            }
            vo.setKeepAlive("0");

            vo.setChildren(vo.getChildren().stream().peek(children -> {
                String childrenPermission = children.getPermission();
                if (StringUtils.isNotBlank(childrenPermission)) {
                    children.setVisible(permissions.contains(childrenPermission) ? "1" : "0");
                } else {
                    children.setVisible("1");
                }
                children.setKeepAlive("0");
            }).collect(Collectors.toList()));

        }).collect(Collectors.toList());
    }

    @Override
    public MenuTkService menuTkService() {
        return menuTkService;
    }
}
