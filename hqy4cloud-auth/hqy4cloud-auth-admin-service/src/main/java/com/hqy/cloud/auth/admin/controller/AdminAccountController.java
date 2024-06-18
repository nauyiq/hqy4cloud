package com.hqy.cloud.auth.admin.controller;

import com.hqy.cloud.auth.admin.annotation.MenuAuthentication;
import com.hqy.cloud.auth.admin.service.RequestAdminAccountService;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.base.vo.AdminUserInfoVO;
import com.hqy.cloud.auth.utils.AuthUtils;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        Long currentUserId = AuthUtils.getCurrentUserId();
        return requestService.getLoginUserInfo(currentUserId);
    }

    @GetMapping("/user/page")
    public R<PageResult<AccountInfoVO>> getAdminUserPage(String username, String nickname, Integer current, Integer size, HttpServletRequest servletRequest) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.getPageUsers(username, nickname, currentUserId, current, size);
    }

    @GetMapping("/user/check/exist")
    public R<Boolean> checkParamExist(String username, String email, String phone) {
        return requestService.checkParamExist(username, email, phone);
    }


    @PostMapping("/user")
    @MenuAuthentication("sys_user_add")
    public R<Boolean> addUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        if (Objects.isNull(userDTO) || !userDTO.checkAddUser()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        Long currentUserId = AuthUtils.getCurrentUserId();
        return requestService.addUser(currentUserId, userDTO);
    }


    @PutMapping("/user")
    @MenuAuthentication("sys_user_edit")
    public R<Boolean> editUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        if (Objects.isNull(userDTO) || !userDTO.checkUpdateUser()) {
            return R.failed();
        }
        Long currentUserId = AuthUtils.getCurrentUserId();
        return requestService.editUser(currentUserId, userDTO);
    }

    @DeleteMapping("/user/{id}")
    @MenuAuthentication("sys_user_del")
    public R<Boolean> deleteUser(HttpServletRequest request, @PathVariable("id")Long id) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        return requestService.deleteUser(currentUserId, id);
    }




}
