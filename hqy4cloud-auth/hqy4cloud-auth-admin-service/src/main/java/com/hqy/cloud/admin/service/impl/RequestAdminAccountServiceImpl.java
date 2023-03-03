package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.admin.service.RequestAdminAccountService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.base.vo.AdminUserInfoVO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.AccountInfoOperationService;
import com.hqy.cloud.auth.service.tk.AccountTkService;
import com.hqy.cloud.auth.service.tk.RoleTkService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
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
    private final AccountTkService accountTkService;
    private final AccountInfoOperationService accountInfoOperationService;
    private final RoleTkService roleTkService;

    @Override
    public DataResponse getLoginUserInfo(Long id) {
        AssertUtil.notNull(id, "Account id should no be null.");
        AccountInfoDTO accountInfo = accountTkService.getAccountInfo(id);
        if (accountInfo == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        String[] roleArrays = StringUtils.tokenizeToStringArray(accountInfo.getRoles(), COMMA);
        List<String> roleList = Arrays.asList(roleArrays);
        List<String> permissions = authOperationService.getManuPermissionsByRoles(roleList);
        AdminUserInfoVO vo = new AdminUserInfoVO(permissions, roleList, new AdminUserInfoVO.SysUser(accountInfo));
        return CommonResultCode.dataResponse(SUCCESS, vo);
    }


    @Override
    public DataResponse getPageUsers(String username, String nickname, Long id, Integer current, Integer size) {
        AssertUtil.notNull(id, "Account id should no be null.");

        Integer maxRoleLevel = accountInfoOperationService.getAccountMaxAuthorityRoleLevel(id);
        PageResult<AccountInfoVO> result = accountTkService.getPageAccountInfos(username, nickname, maxRoleLevel, current, size);
        return CommonResultCode.dataResponse(SUCCESS, result);
    }


    @Override
    public DataResponse getUserRoles(Long id) {
        Integer maxRoleLevel = accountInfoOperationService.getAccountMaxAuthorityRoleLevel(id);
        List<Role> roles = roleTkService.getRolesList(maxRoleLevel, null);
        if (CollectionUtils.isEmpty(roles)) {
            log.warn("Account Roles collections is empty.");
            return CommonResultCode.dataResponse(DATA_EMPTY);
        }
        return CommonResultCode.dataResponse(SUCCESS, roles.stream().map(AccountRoleVO::new).collect(Collectors.toList()));
    }

    @Override
    public DataResponse checkParamExist(String username, String email, String phone) {
        if (!StringUtils.isEmpty(username)) {
            if (accountInfoOperationService.checkParamExist(username, null, null)) {
                return CommonResultCode.dataResponse(false, USERNAME_EXIST, username);
            }
        }

        if (!StringUtils.isEmpty(email)) {
            if (accountInfoOperationService.checkParamExist(null, email, null)) {
                return CommonResultCode.dataResponse(false, EMAIL_EXIST, email);
            }
        }

        if (!StringUtils.isEmpty(phone)) {
            if (accountInfoOperationService.checkParamExist(null, null, phone)) {
                return CommonResultCode.dataResponse(false, PHONE_EXIST, phone);
            }
        }

        return CommonResultCode.dataResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataResponse addUser(Long accessAccountId, UserDTO userDTO) {
        //check params exist.
        DataResponse dataResponse = checkParamExist(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPhone());
        if (!dataResponse.isResult()) {
            return dataResponse;
        }

        //check roles
        List<String> roleNames = userDTO.getRole();
        List<Role> roles = roleTkService.queryRolesByNames(roleNames);
        if (!accountInfoOperationService.checkEnableModifyRoles(accessAccountId, roles)) {
            return CommonResultCode.dataResponse(ERROR_PARAM);
        }

        //registry account;
        if (!accountInfoOperationService.registryAccount(userDTO, roles)) {
            return CommonResultCode.dataResponse(REGISTRY_ACCOUNT_ERROR);
        }

        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse editUser(Long accessAccountId, UserDTO userDTO) {
        //check user exist.
        Account account = accountTkService.queryById(userDTO.getId());
        if (account == null || account.getDeleted()) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }

        //check roles
        List<String> roleNames = userDTO.getRole();
        List<Role> roles = roleTkService.queryRolesByNames(roleNames);
        String accountRoles = account.getRoles();
        List<String> oldRoleNames = Arrays.asList(StringUtils.tokenizeToStringArray(accountRoles, COMMA));
        List<Role> oldRoles = roleTkService.queryRolesByNames(oldRoleNames);
        boolean modifyRoles = true;
        if (oldRoles.size() == roles.size() && oldRoles.containsAll(roles)) {
            log.info("Account roles not modify.");
            modifyRoles = false;
        } else {
            if (!accountInfoOperationService.checkEnableModifyRoles(accessAccountId, roles)) {
                return CommonResultCode.dataResponse(ERROR_PARAM);
            }
        }

        // check params
        String username = userDTO.getUsername();
        boolean checkUsername = !StringUtils.isEmpty(username) && !username.equals(account.getUsername());
        String email = userDTO.getEmail();
        boolean checkEmail = !StringUtils.isEmpty(email) && !email.equals(account.getEmail());
        String phone = userDTO.getEmail();
        boolean checkPhone = !StringUtils.isEmpty(phone) && !phone.equals(account.getPhone());
        DataResponse dataResponse = checkParamExist(checkUsername ? username : null, checkEmail ? email : null, checkPhone ? phone : null);
        if (!dataResponse.isResult()) {
            return dataResponse;
        }

        //edit user
        accountInfoOperationService.editAccount(userDTO, roles, account, modifyRoles ? oldRoles : null);

        return CommonResultCode.dataResponse();
    }

    @Override
    public DataResponse deleteUser(Long accessId, Long id) {
        Account account = accountTkService.queryById(id);
        if (account == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(account.getRoles(), COMMA));
        List<Role> rolesByNames = roleTkService.queryRolesByNames(roles);
        if (!accountInfoOperationService.checkEnableModifyRoles(accessId, rolesByNames)) {
            return CommonResultCode.dataResponse(ERROR_PARAM);
        }

        accountInfoOperationService.deleteUser(account);

        return CommonResultCode.dataResponse();
    }
}
