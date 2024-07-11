package com.hqy.cloud.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hqy.cloud.auth.account.entity.AccountMenu;
import com.hqy.cloud.auth.account.entity.Menu;
import com.hqy.cloud.auth.account.service.AccountMenuService;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.account.service.MenuService;
import com.hqy.cloud.auth.base.converter.MenuConverter;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.auth.service.AuthOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.auth.base.AccountConstants.FIRST_MENU_PARENT_ID;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthOperationServiceImpl implements AuthOperationService {

    private final AccountService accountService;
    private final MenuService menuService;
    private final AccountMenuService accountMenuService;
    private final TransactionTemplate transactionTemplate;


    @Override
    public List<AccountMenu> getAccountMenus(Long accountId) {
        QueryWrapper<AccountMenu> query = Wrappers.query();
        query.eq("account_id", accountId);
        return accountMenuService.list(query);
    }


    @Override
    public List<AdminMenuInfoVO> getAdminMenuInfo(Long id) {
        //获取顶级菜单.
        List<AdminMenuInfoVO> vos = menuService.getAdminMenuInfoByParentId(FIRST_MENU_PARENT_ID);
        vos = setPermissions(vos, getPermissions(id));
        return vos;
    }

    @Override
    public List<AdminTreeMenuVO> getAdminTreeMenu(Long accountId, Boolean status) {
        // 查询菜单.
        QueryWrapper<Menu> query = Wrappers.query();
        query.eq("status", status);
        List<Menu> menus = menuService.list(query);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        // 遍历菜单生成树形结构.
        return menusConvertTreeMenu(getPermissions(accountId), menus);
    }

    private List<String> getPermissions(Long id) {
        return this.getAccountMenus(id).parallelStream().map(AccountMenu::getMenuPermission).toList();
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
        List<Long> ids = menus.stream().map(Menu::getId).toList();
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



}
