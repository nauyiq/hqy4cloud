package com.hqy.cloud.auth.admin.service.impl;

import com.hqy.cloud.auth.admin.service.RequestAdminAccountService;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountMenu;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.base.vo.AdminUserInfoVO;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.hqy.cloud.account.response.AccountResultCode.USER_NOT_FOUND;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminAccountServiceImpl implements RequestAdminAccountService {
    private final AuthOperationService authOperationService;
    private final AccountOperationService accountOperationService;
    private final AccountService accountService;

    @Override
    public R<AdminUserInfoVO> getLoginUserInfo(Long id) {
        AccountInfoDTO accountInfo = accountService.getAccountInfo(id);
        if (Objects.isNull(accountInfo)) {
            return R.failed(USER_NOT_FOUND);
        }
        List<AccountMenu> accountMenus = authOperationService.getAccountMenus(id);
        List<String> permissions = accountMenus.stream().map(AccountMenu::getMenuPermission).toList();
        AdminUserInfoVO vo = new AdminUserInfoVO(permissions, List.of(accountInfo.getRole().name()), new AdminUserInfoVO.SysUser(accountInfo));
        return R.ok(vo);
    }

    @Override
    public R<PageResult<AccountInfoVO>> getPageUsers(String username, String nickname, Long id, Integer current, Integer size) {
        AssertUtil.notNull(id, "Account id should no be null.");
        PageResult<AccountInfoVO> result = accountService.getPageAccountInfos(username, nickname, current, size);
        return R.ok(result);
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
        Account account = Account.register(userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), userDTO.getPhone(),
                StringUtils.isNotBlank(userDTO.getRole()) ? UserRole.valueOf(userDTO.getRole()) : null, userDTO.getAuthorities());
        AccountProfile profile = AccountProfile.register(account.getId(), userDTO.getNickname(), userDTO.getUsername(), userDTO.getPhone(), userDTO.getAvatar());
        return accountOperationService.registryAccount(account, profile) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editUser(Long accessAccountId, UserDTO userDTO) {
        //check user exist.
        Account account = accountService.getById(userDTO.getId());
        if (Objects.isNull(account) || account.getDeleted()) {
            return R.failed(USER_NOT_FOUND);
        }
        R<Boolean> result = checkParamExist(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPhone());
        if (!result.isResult()) {
            return result;
        }
        //edit user
        return accountOperationService.editAccount(userDTO, account) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteUser(Long accessId, Long id) {
        Account account = accountService.getById(id);
        if (Objects.isNull(account)) {
            R.failed(USER_NOT_FOUND);
        }
        return accountOperationService.deleteUser(account) ? R.ok() : R.failed();

    }
}
