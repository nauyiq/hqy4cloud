package com.hqy.cloud.auth.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.base.converter.MenuConverter;
import com.hqy.cloud.auth.base.dto.PermissionDTO;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleMenuDTO;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.auth.core.authentication.support.AuthenticationCacheService;
import com.hqy.cloud.auth.entity.*;
import com.hqy.cloud.auth.mapper.MenuTkMapper;
import com.hqy.cloud.auth.mapper.RoleMenuMapper;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.auth.service.tk.*;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.auth.base.Constants.FIRST_MENU_PARENT_ID;
import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthOperationServiceImpl implements AuthOperationService {

    private final AccountTkService accountTkService;
    private final RoleTkService roleTkService;
    private final RoleResourcesTkService roleResourcesTkService;
    private final ResourceTkService resourceTkService;
    private final MenuTkService menuTkService;
    private final RoleMenuTkService roleMenuTkService;
    private final AuthenticationCacheService roleAuthenticationCacheServer;
    private final TransactionTemplate transactionTemplate;

    @Override
    public boolean checkEnableModifyRoles(Long id, List<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        List<Integer> collect = roles.stream().map(Role::getLevel).collect(Collectors.toList());
        return getAccountMaxAuthorityRoleLevel(id) <= Collections.min(collect);
    }

    @Override
    public int getAccountMaxAuthorityRoleLevel(Long id) {
        Account account = accountTkService.queryById(id);
        AssertUtil.notNull(account, "Account should no be null.");
        List<Role> roles = roleTkService.queryRolesByNames(Arrays.asList(StringUtils.tokenizeToStringArray(account.getRoles(), COMMA)));
        AssertUtil.notEmpty(roles, "Account Roles should no be empty.");
        List<Integer> levelList = roles.stream().map(Role::getLevel).collect(Collectors.toList());
        return Collections.min(levelList);
    }

    @Override
    public List<String> getManuPermissionsByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        List<Integer> ids = roleTkService.selectIdByNames(roles);
        return ((RoleMenuMapper)(roleMenuTkService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<AdminMenuInfoVO> getAdminMenuInfo(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        //获取顶级菜单.
        List<AdminMenuInfoVO> vos = menuTkService.getAdminMenuInfoByParentId(FIRST_MENU_PARENT_ID);
        vos = setPermissions(vos, getPermissions(roles));
        return vos;
    }

    @Override
    public List<AdminTreeMenuVO> getAdminTreeMenu(List<String> roles, Boolean status) {
        // 查询菜单.
        Menu queryMenu = new Menu();
        queryMenu.setStatus(status);
        List<Menu> menus = menuTkService.queryList(queryMenu);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        // 遍历菜单生成树形结构.
        return menusConvertTreeMenu(getPermissions(roles), menus);
    }

    private List<String> getPermissions(List<String> roles) {
        List<Integer> ids = roleTkService.selectIdByNames(roles);
        return ((RoleMenuMapper) (roleMenuTkService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<ResourceDTO>> getAuthoritiesResourcesByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return MapUtil.empty();
        }
        return roleResourcesTkService.getAuthoritiesResourcesByRoles(roles);
    }

    @Override
    public Map<String, List<String>> getPermissionsByRoles(List<String> roles) {
        List<Integer> ids = roleTkService.selectIdByNames(roles);
        List<PermissionDTO> manuPermissionsByRoles = ((RoleMenuMapper) (roleMenuTkService.getTkDao())).getManuPermissionsByRoles(ids);
        if (CollectionUtils.isEmpty(manuPermissionsByRoles)) {
            return MapUtil.empty();
        }
        return manuPermissionsByRoles.stream().collect(Collectors.toMap(PermissionDTO::getRole, PermissionDTO::getPermissions));
    }

    private List<AdminTreeMenuVO> menusConvertTreeMenu(List<String> permissions, List<Menu> menus) {
        // 对parenId相同的进行分组
        Map<Long, List<Menu>> map = menus.stream().collect(Collectors.groupingBy(Menu::getParentId));
        // 获取顶级目录.
        List<Menu> topMenu = map.get(FIRST_MENU_PARENT_ID);
        // 递归遍历获取每个子节点并且赋值
        return findChildrenNode(map, topMenu, permissions);
    }

    List<AdminTreeMenuVO> findChildrenNode(final Map<Long, List<Menu>> map, List<Menu> menus, List<String> permissions) {
        Map<Long, AdminTreeMenuVO> menuMap = menus.stream().map(menu -> menuConvertMenuInfo(menu, permissions)).collect(Collectors.toMap(AdminTreeMenuVO::getId, e -> e));
        List<Long> ids = menus.stream().map(Menu::getId).collect(Collectors.toList());
        for (Long id : ids) {
            if (map.containsKey(id)) {
                List<Menu> menuList = map.get(id);
                List<AdminTreeMenuVO> childrenNode = findChildrenNode(map, menuList, permissions);
                childrenNode.sort(Comparator.comparing(AdminTreeMenuVO::getSortOrder));
                    menuMap.computeIfPresent(id, (key,value) -> {
                        value.setChildren(childrenNode);
                        return value;
                    });
            }
        }

        ArrayList<AdminTreeMenuVO> adminTreeMenuVOS = new ArrayList<>(menuMap.values());
        adminTreeMenuVOS.sort(Comparator.comparing(AdminTreeMenuVO::getSortOrder));
        return adminTreeMenuVOS;
    }



    private AdminTreeMenuVO menuConvertMenuInfo(Menu menu, List<String> permissions) {
        AdminTreeMenuVO treeMenuVo = MenuConverter.CONVERTER.convert(menu);
        String permission = menu.getPermission();
        if (StrUtil.isNotBlank(permission)) {
            treeMenuVo.setVisible(permissions.contains(permission) ? "1" : "0");
        } else {
            treeMenuVo.setVisible("1");
        }
        return treeMenuVo;
    }

    private List<AdminMenuInfoVO> setPermissions(List<AdminMenuInfoVO> vos, List<String> permissions) {
        return vos.stream().peek(vo -> {
            String permission = vo.getPermission();
            if (StrUtil.isNotBlank(permission)) {
                vo.setVisible(permissions.contains(permission) ? "1" : "0");
            } else {
                vo.setVisible("1");
            }

            vo.setChildren(vo.getChildren().stream().peek(children -> {
                String childrenPermission = children.getPermission();
                if (StrUtil.isNotBlank(childrenPermission)) {
                    children.setVisible(permissions.contains(childrenPermission) ? "1" : "0");
                } else {
                    children.setVisible("1");
                }
            }).collect(Collectors.toList()));

        }).collect(Collectors.toList());
    }

    @Override
    public boolean updateRoleMenus(Role role, RoleMenuDTO roleMenus) {
        //获取原来的角色菜单.
        List<RoleMenu> roleMenusEntity = roleMenuTkService.queryList(new RoleMenu(role.getId()));
        List<Integer> menuIds = roleMenus.pauseMenuIds();
        if (CollectionUtils.isEmpty(menuIds) && CollectionUtils.isEmpty(roleMenusEntity)) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(roleMenusEntity) &&
                roleMenusEntity.size() == menuIds.size() && menuIds.containsAll(roleMenusEntity.stream().map(RoleMenu::getMenuId).collect(Collectors.toList()))) {
            return true;
        }

        //修改新的角色菜单
        Boolean result = transactionTemplate.execute(status -> {
            try {
                if (CollectionUtils.isNotEmpty(roleMenusEntity)) {
                    AssertUtil.isTrue(roleMenuTkService.deleteByRoleId(role.getId()), "Failed execute to delete account roles.");
                }
                if (CollectionUtils.isNotEmpty(menuIds)) {
                    AssertUtil.isTrue(roleMenuTkService.insertList(menuIds.stream().map(menuId -> new RoleMenu(role.getId(), menuId)).collect(Collectors.toList()))
                            , "Failed execute to insert role menus.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        if (result) {
            roleAuthenticationCacheServer.invalid(role.getName());
        }
        return result;
    }

    public List<Resource> findResourcesByMenuPermission(List<Integer> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        MenuTkMapper dao = (MenuTkMapper) menuTkService.getTkMapper();
        return dao.queryResourcesByMenuIds(menuIds).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public MenuTkService menuTkService() {
        return menuTkService;
    }

    @Override
    public RoleMenuTkService roleMenuService() {
        return roleMenuTkService;
    }

    @Override
    public ResourceTkService resourceTkService() {
        return resourceTkService;
    }

    @Override
    public RoleResourcesTkService roleResourcesTkService() {
        return roleResourcesTkService;
    }
}
