package com.hqy.admin.service.impl.request;

import com.hqy.admin.service.request.AdminOperationService;
import com.hqy.auth.dao.RoleMenuDao;
import com.hqy.auth.service.AccountRoleTkService;
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

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOperationServiceImpl implements AdminOperationService {

    private final AccountRoleTkService accountRoleTkService;
    private final MenuTkService menuTkService;
    private final RoleMenuService roleMenuService;

    @Override
    public List<String> getManuPermissionsByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        List<Integer> ids = accountRoleTkService.selectIdByNames(roles);
        return ((RoleMenuDao)(roleMenuService.getTkDao())).getManuPermissionsByRoleIds(ids)
                .stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }
}
