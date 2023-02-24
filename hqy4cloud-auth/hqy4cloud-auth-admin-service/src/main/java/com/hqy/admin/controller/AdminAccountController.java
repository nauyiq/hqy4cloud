package com.hqy.admin.controller;

import com.hqy.admin.service.request.AdminAccountRequestService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.dto.UserDTO;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.hqy.cloud.common.result.CommonResultCode.USER_NOT_FOUND;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 16:58
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminAccountRequestService requestService;

    @GetMapping("/userInfo")
    public DataResponse getAdminLoginUserInfo(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        return requestService.getLoginUserInfo(id);
    }

    @GetMapping("/user/page")
    public DataResponse getAdminUserPage(String username, String nickname, Integer current, Integer size, HttpServletRequest servletRequest) {
        Long id = OauthRequestUtil.idFromOauth2Request(servletRequest);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.getPageUsers(username, nickname, id, current, size);
    }

    @GetMapping("/user/roles")
    public DataResponse getUserRoles(HttpServletRequest servletRequest) {
        Long id = OauthRequestUtil.idFromOauth2Request(servletRequest);
        if (id == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        return requestService.getUserRoles(id);
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
        Long accessAccountId = OauthRequestUtil.idFromOauth2Request(request);
        return requestService.addUser(accessAccountId, userDTO);
    }


    @PutMapping("/user")
    public DataResponse editUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        if (userDTO == null || !userDTO.checkUpdateUser()) {
            return CommonResultCode.dataResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        Long accessAccountId = OauthRequestUtil.idFromOauth2Request(request);
        return requestService.editUser(accessAccountId, userDTO);
    }

    @DeleteMapping("/user/{id}")
    public DataResponse deleteUser(HttpServletRequest request, @PathVariable("id")Long id) {
        Long accessId = OauthRequestUtil.idFromOauth2Request(request);
        if (id == null || accessId == null) {
            return CommonResultCode.dataResponse(USER_NOT_FOUND);
        }
        return requestService.deleteUser(accessId, id);
    }




}
