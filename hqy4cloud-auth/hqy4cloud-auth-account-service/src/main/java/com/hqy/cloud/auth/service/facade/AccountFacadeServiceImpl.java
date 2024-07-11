package com.hqy.cloud.auth.service.facade;

import com.hqy.cloud.account.request.AccountQueryParams;
import com.hqy.cloud.account.response.AccountInfo;
import com.hqy.cloud.account.service.AccountFacadeService;
import com.hqy.cloud.auth.account.entity.convertor.AccountConvertor;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.enums.AccountResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.rpc.dubbo.DubboConstants;
import com.hqy.cloud.rpc.dubbo.facade.Facade;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
@RequiredArgsConstructor
@DubboService(version = DubboConstants.DEFAULT_DUBBO_SERVICE_VERSION)
public class AccountFacadeServiceImpl implements AccountFacadeService {
    private final AccountService accountService;

    @Facade
    @Override
    public R<AccountInfo> query(AccountQueryParams queryParams) {
        AccountInfoDTO account = null;
        Long id = queryParams.getId();
        if (id != null) {
            account = accountService.getAccountInfo(id);
        } else if (StringUtils.isNotBlank(queryParams.getPhone())) {
            account = accountService.getAccountInfo(queryParams.getPhone());
        } else if (StringUtils.isNotBlank(queryParams.getEmail())) {
            account = accountService.getAccountInfo(queryParams.getEmail());
        }
        if (account == null) {
            return R.failed(AccountResultCode.USER_NOT_FOUND);
        }

        AccountInfo accountInfo = AccountConvertor.CONVERTOR.mapToVo(account);
        return R.ok(accountInfo);
    }
}
