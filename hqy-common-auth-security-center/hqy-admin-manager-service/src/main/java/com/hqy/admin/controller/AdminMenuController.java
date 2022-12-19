package com.hqy.admin.controller;

import com.hqy.admin.service.request.AdminMenuRequestService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.AssertUtil;
import com.hqy.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 获取这个角色拥有菜单权限jihe
     * @param roleId
     * @return
     */
    @GetMapping("/menu/tree/{roleId}")
    public DataResponse getMenuPermissionsIdByRoleId(@PathVariable("roleId") Integer roleId) {
        if (roleId == null) {
            return CommonResultCode.dataResponse(CommonResultCode.NOT_FOUND_ROLE);
        }
        return requestService.getMenuPermissionsIdByRoleId(roleId);
    }

    @GetMapping("/menu/tree")
    public DataResponse getTreeMenu(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        AssertUtil.notNull(id, "Access account id should not be null.");
        return requestService.getAdminTreeMenu(id);
    }







}
