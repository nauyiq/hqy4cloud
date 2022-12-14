package com.hqy.admin.controller;

import com.hqy.admin.service.request.AdminMenuRequestService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.util.AssertUtil;
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
 * @date 2022/12/12 10:24
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuRequestService requestService;

    @GetMapping("/menu")
    public DataResponse getMenu(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        AssertUtil.notNull(id, "Access account id should not be null.");
        return requestService.getAdminMenu(id);
    }





}
