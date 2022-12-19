package com.hqy.admin.service.impl.request;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.admin.service.AdminOperationService;
import com.hqy.admin.service.request.AdminMenuRequestService;
import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.common.vo.menu.AdminTreeMenuVo;
import com.hqy.auth.entity.Role;
import com.hqy.auth.entity.RoleMenu;
import com.hqy.auth.service.AccountAuthService;
import com.hqy.auth.service.AccountTkService;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.base.common.result.CommonResultCode.NOT_FOUND_ROLE;
import static com.hqy.base.common.result.CommonResultCode.SUCCESS;

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
    public DataResponse getAdminTreeMenu(Long id) {
        AccountInfoDTO accountInfo = accountAuthService.getAccountTkService().getAccountInfo(id);
        if (accountInfo == null) {
            return CommonResultCode.dataResponse(CommonResultCode.USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA));
        List<AdminTreeMenuVo> adminTreeMenuVos = operationService.getAdminTreeMenu(roles);
        return CommonResultCode.dataResponse(CommonResultCode.SUCCESS, adminTreeMenuVos);
    }
}
