package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.admin.service.RequestAdminRoleService;
import com.hqy.cloud.auth.account.service.RoleMenuService;
import com.hqy.cloud.auth.account.service.RoleService;
import com.hqy.cloud.auth.base.dto.RoleDTO;
import com.hqy.cloud.auth.base.dto.RoleMenuDTO;
import com.hqy.cloud.auth.base.enums.AccountResultCode;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.account.entity.AccountRole;
import com.hqy.cloud.auth.account.entity.Role;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

import static com.hqy.cloud.common.result.ResultCode.NOT_FOUND_ROLE;
import static com.hqy.cloud.common.result.ResultCode.ROLE_NAME_EXIST;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/14 15:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminRoleServiceImpl implements RequestAdminRoleService {

    private final TransactionTemplate transactionTemplate;
    private final RoleService roleService;
    private final RoleMenuService roleMenuService;
    private final AuthOperationService authoperationService;
    private final AccountOperationService accountOperationService;

    @Override
    public R<PageResult<AccountRoleVO>> getPageRoles(String roleName, String note, Long id, Integer current, Integer size) {
        Integer maxRoleLevel = authoperationService.getAccountMaxAuthorityRoleLevel(id);
        PageResult<AccountRoleVO> result = accountOperationService.getRoleTkService().getPageRoles(roleName, note, maxRoleLevel, current, size);
        return R.ok(result);
    }

    @Override
    public R<List<Role>> getRoles(Long id) {
        Integer maxRoleLevel = authoperationService.getAccountMaxAuthorityRoleLevel(id);
        List<Role> rolesList = accountOperationService.getRoleTkService().getRolesList(maxRoleLevel, true);
        return R.ok(rolesList);
    }

    @Override
    public R<Boolean> checkLevel(Long id, Integer level) {
        int maxAuthorityRoleLevel = authoperationService.getAccountMaxAuthorityRoleLevel(id);
        return maxAuthorityRoleLevel <= level ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> checkRoleNameExist(String roleName) {
        Role role = accountOperationService.getRoleTkService().queryOne(new Role(roleName));
        return Objects.nonNull(role) ? R.failed(ROLE_NAME_EXIST) : R.ok();
    }

    @Override
    public R<Boolean> addRole(Long id, RoleDTO roleDTO) {
        R<Boolean> r = checkLevel(id, roleDTO.getLevel());
        if (!r.isResult()) {
            return r;
        }
        Role role = accountOperationService.getRoleTkService().queryOne(new Role(roleDTO.getName()));
        if (Objects.nonNull(role)) {
            return R.failed(ROLE_NAME_EXIST);
        } else {
            role = new Role(roleDTO.getName(), roleDTO.getLevel(), roleDTO.getNote());
        }

        return accountOperationService.getRoleTkService().insert(role) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editRole(Long id, RoleDTO role) {
        Role oldRole = accountOperationService.getRoleTkService().queryById(role.getId());
        if (Objects.isNull(oldRole)) {
            return R.failed(NOT_FOUND_ROLE);
        }

        oldRole.setNote(role.getNote());
        Integer level = role.getLevel();
        boolean modifyLevel = Objects.nonNull(level) && !level.equals(oldRole.getLevel());
        List<AccountRole> accountRoles = null;
        if (modifyLevel) {
            oldRole.setLevel(level);
            accountRoles = accountOperationService.getAccountRoleTkService().queryList(new AccountRole(role.getId()));
        }

        List<AccountRole> finalAccountRoles = accountRoles;
        Boolean result = transactionTemplate.execute(status -> {
            try {
                if (modifyLevel) {
                    if (CollectionUtils.isNotEmpty(finalAccountRoles)) {
                        AssertUtil.isTrue(accountOperationService.getAccountRoleTkService().updateRoleLevel(oldRole.getId(), level), "Failed execute to update account Role");
                    }
                }
                AssertUtil.isTrue(accountOperationService.getRoleTkService().update(oldRole), "Failed execute to update role.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        return Boolean.TRUE.equals(result) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteRole(Integer roleId) {
        Role role = roleService.getById(roleId);
        if (Objects.isNull(role)) {
            return R.failed(AccountResultCode.NOT_FOUND_ROLE);
        }
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountOperationService.deleteAccountRole(role), "Failed execute to delete role, roleId = " + roleId);
                AssertUtil.isTrue(roleMenuService.deleteByRoleId(roleId), "Failed execute to delete role menu");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        if (Objects.isNull(result) || !result) {
            return R.failed();
        }
        return R.ok();
    }

    @Override
    public R<Boolean> updateRoleMenus(RoleMenuDTO roleMenus) {
        Integer roleId = roleMenus.getRoleId();
        Role role = accountOperationService.getRoleTkService().queryById(roleId);
        if (Objects.isNull(role)) {
            return R.failed(NOT_FOUND_ROLE);
        }
        return authoperationService.updateRoleMenus(role, roleMenus) ? R.ok() : R.failed();
    }
}
