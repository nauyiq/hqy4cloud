package com.hqy.admin.service.impl.request;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.admin.service.AdminOperationService;
import com.hqy.admin.service.request.AdminMenuRequestService;
import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.service.AccountTkService;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMenuRequestServiceImpl implements AdminMenuRequestService {

    private final AdminOperationService operationService;
    private final AccountTkService accountTkService;

    @Override
    public DataResponse getAdminMenu(Long id) {
        AccountInfoDTO accountInfo = accountTkService.getAccountInfo(id);
        if (accountInfo == null) {
            return CommonResultCode.dataResponse(CommonResultCode.USER_NOT_FOUND);
        }
        List<String> roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA));
        List<AdminMenuInfoVO> adminMenuInfo = operationService.getAdminMenuInfo(roles);
        return CommonResultCode.dataResponse(CommonResultCode.SUCCESS, adminMenuInfo);
    }
}
