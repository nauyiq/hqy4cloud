package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminAccountService;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    public DataResponse getAdminLoginUserInfo(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.getLoginUserInfo(authentication.getId());
    }

    @GetMapping("/user/page")
    public DataResponse getAdminUserPage(String username, String nickname, Integer current, Integer size, HttpServletRequest servletRequest) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(servletRequest);
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.getPageUsers(username, nickname, authentication.getId(), current, size);
    }

    @GetMapping("/user/roles")
    public DataResponse getUserRoles(HttpServletRequest servletRequest) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(servletRequest);
        return requestService.getUserRoles(authentication.getId());
    }

    @GetMapping("/user/check/exist")
    public DataResponse checkParamExist(String username, String email, String phone) {
        if (StringUtils.isAllBlank(username, email, phone)) {
            return CommonResultCode.dataResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.checkParamExist(username, email, phone);
    }


    @PostMapping("/user")
    public DataResponse addUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        if (userDTO == null || !userDTO.checkAddUser()) {
            return CommonResultCode.dataResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.addUser(authentication.getId(), userDTO);
    }


    @PutMapping("/user")
    public DataResponse editUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        if (userDTO == null || !userDTO.checkUpdateUser()) {
            return CommonResultCode.dataResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.editUser(authentication.getId(), userDTO);
    }

    @DeleteMapping("/user/{id}")
    public DataResponse deleteUser(HttpServletRequest request, @PathVariable("id")Long id) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        return requestService.deleteUser(authentication.getId(), id);
    }




}
