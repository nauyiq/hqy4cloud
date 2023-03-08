package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminAccountService;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.base.vo.AdminUserInfoVO;
import com.hqy.cloud.auth.core.authentication.PreAuthentication;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 后台管理-账号相关接口API
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 16:58
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAccountController {

    private final RequestAdminAccountService requestService;

    @GetMapping("/userInfo")
    public R<AdminUserInfoVO> getAdminLoginUserInfo(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.getLoginUserInfo(authentication.getId());
    }

    @GetMapping("/user/page")
    public R<PageResult<AccountInfoVO>> getAdminUserPage(String username, String nickname, Integer current, Integer size, HttpServletRequest servletRequest) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(servletRequest);
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.getPageUsers(username, nickname, authentication.getId(), current, size);
    }

    @GetMapping("/user/roles")
    public R<List<AccountRoleVO>> getUserRoles(HttpServletRequest servletRequest) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(servletRequest);
        return requestService.getUserRoles(authentication.getId());
    }

    @GetMapping("/user/check/exist")
    public R<Boolean> checkParamExist(String username, String email, String phone) {
        return requestService.checkParamExist(username, email, phone);
    }


    @PostMapping("/user")
    @PreAuthentication("sys_user_add")
    public R<Boolean> addUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        if (Objects.isNull(userDTO) || !userDTO.checkAddUser()) {
            return R.failed(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.addUser(authentication.getId(), userDTO);
    }


    @PutMapping("/user")
    @PreAuthentication("sys_user_edit")
    public R<Boolean> editUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        if (Objects.isNull(userDTO) || !userDTO.checkUpdateUser()) {
            return R.failed();
        }
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.editUser(authentication.getId(), userDTO);
    }

    @DeleteMapping("/user/{id}")
    @PreAuthentication("sys_user_del")
    public R<Boolean> deleteUser(HttpServletRequest request, @PathVariable("id")Long id) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.deleteUser(authentication.getId(), id);
    }




}
