package com.hqy.admin.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.admin.service.AdminAccountRequestService;
import com.hqy.admin.service.request.AdminOperationService;
import com.hqy.admin.vo.AdminUserInfoVO;
import com.hqy.auth.service.AccountTkService;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 18:55
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAccountRequestServiceImpl implements AdminAccountRequestService {

    private final AdminOperationService adminOperationService;
    private final AccountTkService accountTkService;

    @Override
    public DataResponse getLoginUserInfo(Long id) {
        AssertUtil.notNull(id, "Account id should no be null.");
        AccountInfoDTO accountInfo = accountTkService.getAccountInfo(id);
        if (accountInfo == null) {
            return CommonResultCode.dataResponse(CommonResultCode.USER_NOT_FOUND);
        }
        String[] roleArrays = StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA);
        List<String> roleList = Arrays.asList(roleArrays);
        List<String> permissions = adminOperationService.getManuPermissionsByRoles(roleList);
        AdminUserInfoVO vo = new AdminUserInfoVO(permissions, roleList, new AdminUserInfoVO.SysUser(accountInfo));
        return CommonResultCode.dataResponse(CommonResultCode.SUCCESS, vo);
    }




}
