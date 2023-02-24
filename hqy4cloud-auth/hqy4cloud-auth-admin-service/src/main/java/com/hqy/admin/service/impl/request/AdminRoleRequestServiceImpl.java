package com.hqy.admin.service.impl.request;

import com.hqy.admin.service.AdminOperationService;
import com.hqy.admin.service.request.AdminRoleRequestService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.dto.RoleDTO;
import com.hqy.cloud.common.dto.RoleMenuDTO;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.vo.AccountRoleVO;
import com.hqy.cloud.entity.AccountRole;
import com.hqy.cloud.entity.Role;
import com.hqy.cloud.service.AccountAuthService;
import com.hqy.cloud.service.AccountInfoOperationService;
import com.hqy.cloud.service.RoleTkService;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hqy.cloud.common.result.CommonResultCode.*;

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
        PageResult<AccountRoleVO> result = accountAuthService.getRoleTkService().getPageRoles(roleName, note, maxRoleLevel, current, size);
        return CommonResultCode.dataResponse(SUCCESS, result);
    }

    @Override
    public DataResponse getRoles(Long id) {
        Integer maxRoleLevel = accountInfoOperationService.getAccountMaxAuthorityRoleLevel(id);
        List<Role> rolesList = accountAuthService.getRoleTkService().getRolesList(maxRoleLevel, true);
        return CommonResultCode.dataResponse(rolesList);
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
