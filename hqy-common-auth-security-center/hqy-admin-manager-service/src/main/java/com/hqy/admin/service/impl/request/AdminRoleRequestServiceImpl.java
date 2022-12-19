package com.hqy.admin.service.impl.request;

import com.hqy.admin.service.AdminOperationService;
import com.hqy.admin.service.request.AdminRoleRequestService;
import com.hqy.auth.common.dto.RoleDTO;
import com.hqy.auth.common.dto.RoleMenuDTO;
import com.hqy.auth.common.vo.AccountRoleVO;
import com.hqy.auth.entity.AccountRole;
import com.hqy.auth.entity.Role;
import com.hqy.auth.service.AccountAuthService;
import com.hqy.auth.service.AccountInfoOperationService;
import com.hqy.auth.service.RoleTkService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.result.PageResult;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hqy.base.common.result.CommonResultCode.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/14 15:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRoleRequestServiceImpl implements AdminRoleRequestService {

    private final AccountInfoOperationService accountInfoOperationService;
    private final AdminOperationService operationService;
    private final AccountAuthService accountAuthService;

    @Override
    public DataResponse getPageRoles(String roleName, String note, Long id, Integer current, Integer size) {
        AssertUtil.notNull(id, "Account id should no be null.");

        Integer maxRoleLevel = accountInfoOperationService.getAccountMaxAuthorityRoleLevel(id);
        PageResult<AccountRoleVO> accountRoleVOPageResult = accountAuthService.getRoleTkService().getPageRoles(roleName, note, maxRoleLevel, current, size);
        return CommonResultCode.dataResponse(SUCCESS, accountRoleVOPageResult);
    }

    @Override
    public DataResponse checkLevel(Long id, Integer level) {
        int maxAuthorityRoleLevel = accountInfoOperationService.getAccountMaxAuthorityRoleLevel(id);
        if (maxAuthorityRoleLevel <= level) {
            return CommonResultCode.dataResponse();
        }
        return CommonResultCode.dataResponse(LIMITED_SETTING_ROLE_LEVEL);
    }

    @Override
    public DataResponse checkRoleNameExist(String roleName) {
        Role role = accountAuthService.getRoleTkService().queryOne(new Role(roleName));
        if (role != null) {
            return CommonResultCode.dataResponse(ROLE_NAME_EXIST);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse addRole(Long id, RoleDTO roleDTO) {
        DataResponse dataResponse = checkLevel(id, roleDTO.getLevel());
        if (!dataResponse.isResult()) {
            return dataResponse;
        }
        Role role = accountAuthService.getRoleTkService().queryOne(new Role(roleDTO.getName()));
        if (role != null) {
            return CommonResultCode.dataResponse(ROLE_NAME_EXIST);
        } else {
            role = new Role(roleDTO.getName(), roleDTO.getLevel(), roleDTO.getNote());
        }

        if (!accountAuthService.getRoleTkService().insert(role)) {
            return CommonResultCode.dataResponse(SYSTEM_ERROR_INSERT_FAIL);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataResponse editRole(Long id, RoleDTO role) {
        Role roleEntity = accountAuthService.getRoleTkService().queryById(role.getId());
        if (roleEntity == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_ROLE);
        }

        if (!role.getLevel().equals(roleEntity.getLevel())) {
            roleEntity.setLevel(role.getLevel());
            List<AccountRole> accountRoles = accountAuthService.getAccountRoleTkService().queryList(new AccountRole(role.getId()));
            if (CollectionUtils.isNotEmpty(accountRoles)) {
                AssertUtil.isTrue(accountAuthService.getAccountRoleTkService().updateRoleLevel(roleEntity.getId(), roleEntity.getLevel()), "Failed execute to update account Role");
            }
        }

        roleEntity.setNote(role.getNote());
        AssertUtil.isTrue(accountAuthService.getRoleTkService().update(roleEntity), "Failed execute to update role.");
        return CommonResultCode.dataResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataResponse deleteRole(Integer roleId) {
        RoleTkService roleTkService = accountAuthService.getRoleTkService();
        Role role = roleTkService.queryById(roleId);
        if (role == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_ROLE);
        }

        if (!accountAuthService.deleteRole(role)) {
            return CommonResultCode.dataResponse(SYSTEM_BUSY);
        }

        // delete role_menu
        AssertUtil.isTrue(operationService.roleMenuService().deleteByRoleId(roleId), "Failed execute to delete role menu");

        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse updateRoleMenus(RoleMenuDTO roleMenus) {
        Integer roleId = roleMenus.getRoleId();
        Role role = accountAuthService.getRoleTkService().queryById(roleId);
        if (role == null) {
            return CommonResultCode.dataResponse(NOT_FOUND_ROLE);
        }

        if (!operationService.updateRoleMenus(role, roleMenus)) {
            return CommonResultCode.dataResponse(SYSTEM_BUSY);
        }
        return CommonResultCode.dataResponse(SUCCESS);
    }
}
