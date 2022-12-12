package com.hqy.admin.controller;

import com.hqy.admin.service.AdminAccountRequestService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
            return CommonResultCode.dataResponse(CommonResultCode.USER_NOT_FOUND);
        }
        return requestService.getLoginUserInfo(id);
    }




}
