package com.hqy.cloud.admin.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.admin.service.RequestAdminAccountService;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.base.vo.AdminUserInfoVO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.auth.service.tk.AccountTkService;
import com.hqy.cloud.auth.service.tk.RoleTkService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;
import static com.hqy.cloud.common.result.CommonResultCode.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 18:55
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminAccountServiceImpl implements RequestAdminAccountService {

    private final AuthOperationService authOperationService;
    private final AccountOperationService accountOperationService;
    private final AccountTkService accountTkService;
    private final RoleTkService roleTkService;

    @Override
    public R<AdminUserInfoVO> getLoginUserInfo(Long id) {
        AccountInfoDTO accountInfo = accountTkService.getAccountInfo(id);
        if (Objects.isNull(accountInfo)) {
            return R.failed(USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), COMMA));
        List<String> permissions = authOperationService.getManuPermissionsByRoles(roles);
        AdminUserInfoVO vo = new AdminUserInfoVO(permissions, roles, new AdminUserInfoVO.SysUser(accountInfo));
        return R.ok(vo);
    }


    @Override
    public R<PageResult<AccountInfoVO>> getPageUsers(String username, String nickname, Long id, Integer current, Integer size) {
        AssertUtil.notNull(id, "Account id should no be null.");
        Integer maxRoleLevel = authOperationService.getAccountMaxAuthorityRoleLevel(id);
        PageResult<AccountInfoVO> result = accountTkService.getPageAccountInfos(username, nickname, maxRoleLevel, current, size);
        return R.ok(result);
    }


    @Override
    public R<List<AccountRoleVO>> getUserRoles(Long id) {
        Integer maxRoleLevel = authOperationService.getAccountMaxAuthorityRoleLevel(id);
        List<Role> roles = roleTkService.getRolesList(maxRoleLevel, null);
        if (CollectionUtils.isEmpty(roles)) {
            log.warn("Account Roles collections is empty.");
            return R.failed(DATA_EMPTY);
        }
        return R.ok(roles.stream().map(AccountRoleVO::new).collect(Collectors.toList()));
    }

    @Override
    public R<Boolean> checkParamExist(String username, String email, String phone) {
        boolean result = accountOperationService.checkParamExist(username, email, phone);
        return result ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> addUser(Long accessAccountId, UserDTO userDTO) {
        //check params exist.
        R<Boolean> result = checkParamExist(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPhone());
        if (!result.isResult()) {
            return result;
        }

        //check roles
        List<Role> roles = roleTkService.queryRolesByNames(userDTO.getRole());
        if (!authOperationService.checkEnableModifyRoles(accessAccountId, roles)) {
            return R.failed(LIMITED_SETTING_ROLE_LEVEL);
        }

        boolean ok = accountOperationService.registryAccount(userDTO, roles);
        return ok ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editUser(Long accessAccountId, UserDTO userDTO) {
        //check user exist.
        Account account = accountTkService.queryById(userDTO.getId());
        if (Objects.isNull(account) || account.getDeleted()) {
            return R.failed(USER_NOT_FOUND);
        }

        //check roles
        List<Role> roles = roleTkService.queryRolesByNames(userDTO.getRole());
        String accountRoles = account.getRoles();
        List<String> oldRoleNames = Arrays.asList(StringUtils.tokenizeToStringArray(accountRoles, COMMA));
        List<Role> oldRoles = roleTkService.queryRolesByNames(oldRoleNames);
        boolean modifyRoles = true;
        if (oldRoles.size() == roles.size() && oldRoles.containsAll(roles)) {
            modifyRoles = false;
        } else {
            if (!authOperationService.checkEnableModifyRoles(accessAccountId, roles)) {
                return R.failed(LIMITED_SETTING_ROLE_LEVEL);
            }
        }
        R<Boolean> booleanR = checkParamExist(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPhone());
        if (!booleanR.isResult()) {
            return booleanR;
        }

        //edit user
        boolean result = accountOperationService.editAccount(userDTO, roles, account, modifyRoles ? oldRoles : null);
        return result ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteUser(Long accessId, Long id) {
        Account account = accountTkService.queryById(id);
        if (Objects.isNull(account)) {
            R.failed(USER_NOT_FOUND);
        }
        List<Role> roles = roleTkService.queryRolesByNames(Arrays.asList(StringUtils.tokenizeToStringArray(account.getRoles(), COMMA)));
        if (!authOperationService.checkEnableModifyRoles(accessId, roles)) {
            return R.failed(ERROR_PARAM);
        }

        return accountOperationService.deleteUser(account) ? R.ok() : R.failed();

    }
}
