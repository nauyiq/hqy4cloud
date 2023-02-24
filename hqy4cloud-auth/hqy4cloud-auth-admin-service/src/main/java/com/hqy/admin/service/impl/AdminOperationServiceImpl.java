package com.hqy.admin.service.impl;

import com.hqy.access.auth.support.ResourceInRoleCacheServer;
import com.hqy.admin.service.AdminOperationService;
import com.hqy.cloud.common.convert.MenuConverter;
import com.hqy.cloud.common.dto.RoleMenuDTO;
import com.hqy.cloud.common.vo.menu.AdminMenuInfoVO;
import com.hqy.cloud.common.vo.menu.AdminTreeMenuVo;
import com.hqy.cloud.mapper.MenuTkMapper;
import com.hqy.cloud.mapper.RoleMenuMapper;
import com.hqy.cloud.entity.Menu;
import com.hqy.cloud.entity.Resource;
import com.hqy.cloud.entity.Role;
import com.hqy.cloud.entity.RoleMenu;
import com.hqy.cloud.service.AccountAuthService;
import com.hqy.cloud.service.MenuTkService;
import com.hqy.cloud.service.RoleMenuService;
import com.hqy.cloud.service.RoleTkService;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.Constants.FIRST_MENU_PARENT_ID;

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
    private final AccountAuthService accountAuthService;
    private final ResourceInRoleCacheServer resourceInRoleCacheServer;

    @Override
    public List<String> getManuPermissionsByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        List<Integer> ids = roleTkService.selectIdByNames(roles);
        return ((RoleMenuMapper)(roleMenuService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuInfoVO> getAdminMenuInfo(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        //获取顶级菜单.
        List<AdminMenuInfoVO> vos = menuTkService.getAdminMenuInfoByParentId(FIRST_MENU_PARENT_ID);
        if (CollectionUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }
        //设置权限.
        List<Integer> ids = roleTkService.selectIdByNames(roles);
        List<String> permissions = ((RoleMenuMapper) (roleMenuService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        vos = setPermissions(vos, permissions);
        return vos;
    }

    @Override
    public List<AdminTreeMenuVo> getAdminTreeMenu(List<String> roles, Boolean status) {
        // 查询菜单.
        Menu queryMenu = new Menu();
        queryMenu.setStatus(status);
        List<Menu> menus = menuTkService.queryList(queryMenu);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        //获取账号权限
        List<Integer> ids = roleTkService.selectIdByNames(roles);
        List<String> permissions = ((RoleMenuMapper) (roleMenuService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

        // 遍历菜单生成树形结构.
        return menusConvertTreeMenu(permissions, menus);
    }

    private List<AdminTreeMenuVo> menusConvertTreeMenu(List<String> permissions, List<Menu> menus) {
        // 对parenId相同的进行分组
        Map<Long, List<Menu>> map = menus.stream().collect(Collectors.groupingBy(Menu::getParentId));
        // 获取顶级目录.
        List<Menu> topMenu = map.get(FIRST_MENU_PARENT_ID);
        // 递归遍历获取每个子节点并且赋值
        return findChildrenNode(map, topMenu, permissions);
    }

    List<AdminTreeMenuVo> findChildrenNode(final Map<Long, List<Menu>> map, List<Menu> menus, List<String> permissions) {
        Map<Long, AdminTreeMenuVo> menuMap = menus.stream().map(menu -> menuConvertMenuInfo(menu, permissions)).collect(Collectors.toMap(AdminTreeMenuVo::getId, e -> e));
        List<Long> ids = menus.stream().map(Menu::getId).collect(Collectors.toList());
        for (Long id : ids) {
            if (map.containsKey(id)) {
                List<Menu> menuList = map.get(id);
                List<AdminTreeMenuVo> childrenNode = findChildrenNode(map, menuList, permissions);
                childrenNode.sort(Comparator.comparing(AdminTreeMenuVo::getSortOrder));
                    menuMap.computeIfPresent(id, (key,value) -> {
                        value.setChildren(childrenNode);
                        return value;
                    });
            }
        }

        ArrayList<AdminTreeMenuVo> adminTreeMenuVos = new ArrayList<>(menuMap.values());
        adminTreeMenuVos.sort(Comparator.comparing(AdminTreeMenuVo::getSortOrder));
        return adminTreeMenuVos;
    }



    private AdminTreeMenuVo menuConvertMenuInfo(Menu menu, List<String> permissions) {
        AdminTreeMenuVo treeMenuVo = MenuConverter.CONVERTER.convert(menu);
        String permission = menu.getPermission();
        if (StringUtils.isNotBlank(permission)) {
            treeMenuVo.setVisible(permissions.contains(permission) ? "1" : "0");
        } else {
            treeMenuVo.setVisible("1");
        }
        return treeMenuVo;
    }

    private List<AdminMenuInfoVO> setPermissions(List<AdminMenuInfoVO> vos, List<String> permissions) {
        return vos.stream().peek(vo -> {
            String permission = vo.getPermission();
            if (StringUtils.isNotBlank(permission)) {
                vo.setVisible(permissions.contains(permission) ? "1" : "0");
            } else {
                vo.setVisible("1");
            }

            vo.setChildren(vo.getChildren().stream().peek(children -> {
                String childrenPermission = children.getPermission();
                if (StringUtils.isNotBlank(childrenPermission)) {
                    children.setVisible(permissions.contains(childrenPermission) ? "1" : "0");
                } else {
                    children.setVisible("1");
                }
            }).collect(Collectors.toList()));

        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleMenus(Role role, RoleMenuDTO roleMenus) {
        //获取原来的角色菜单.
        List<RoleMenu> roleMenusEntity = roleMenuService.queryList(new RoleMenu(role.getId()));
        List<Integer> menuIds = roleMenus.pauseMenuIds();
        if (CollectionUtils.isEmpty(menuIds) && CollectionUtils.isEmpty(roleMenusEntity)) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(roleMenusEntity) &&
                roleMenusEntity.size() == menuIds.size() && menuIds.containsAll(roleMenusEntity.stream().map(RoleMenu::getMenuId).collect(Collectors.toList()))) {
            return true;
        }

        boolean isUpdateRoleMenus = false;

        //修改新的角色菜单
        if (CollectionUtils.isNotEmpty(roleMenusEntity)) {
            AssertUtil.isTrue(roleMenuService.deleteByRoleId(role.getId()), "Failed execute to delete account roles.");
            isUpdateRoleMenus = true;
        }
        if (CollectionUtils.isNotEmpty(menuIds)) {
            AssertUtil.isTrue(roleMenuService.insertList(menuIds.stream().map(menuId -> new RoleMenu(role.getId(), menuId)).collect(Collectors.toList()))
                    , "Failed execute to insert role menus.");
            isUpdateRoleMenus = true;
        }

        //如果菜单中存在permission， 则表示当前菜单需要修改角色资源表
        if (isUpdateRoleMenus) {
            List<Resource> resources = findResourcesByMenuPermission(menuIds);
            AssertUtil.isTrue(accountAuthService.modifyRoleResources(role, resources), "Failed execute to update role resources.");
            //删除缓存.
            resourceInRoleCacheServer.invalid(role.getName());
        }

        return true;
    }

    public List<Resource> findResourcesByMenuPermission(List<Integer> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        MenuTkMapper dao = (MenuTkMapper) menuTkService.getTkDao();
        return dao.queryResourcesByMenuIds(menuIds).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public MenuTkService menuTkService() {
        return menuTkService;
    }

    @Override
    public RoleMenuService roleMenuService() {
        return roleMenuService;
    }
}
