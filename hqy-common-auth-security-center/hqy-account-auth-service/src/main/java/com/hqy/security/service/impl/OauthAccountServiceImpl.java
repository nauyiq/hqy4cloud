package com.hqy.security.service.impl;

import com.hqy.auth.entity.Account;
import com.hqy.auth.entity.AccountOauthClient;
import com.hqy.auth.service.AccountOauthClientTkService;
import com.hqy.auth.service.AccountTkService;
import com.hqy.security.dto.OauthAccountRegistryDTO;
import com.hqy.security.service.OauthAccountService;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.AssertUtil;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 16:04
 */
@Service
public class OauthAccountServiceImpl implements OauthAccountService {

    @Resource
    private AccountTkService accountTkService;

    @Resource
    private AccountOauthClientTkService accountOauthClientTkService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResponse registry(OauthAccountRegistryDTO oauthAccount) {
        String username = oauthAccount.getUsername();
        //check username exist.
        Account account = accountTkService.queryOne(new Account(username));
        if (Objects.nonNull(account)) {
            return CommonResultCode.messageResponse(CommonResultCode.USERNAME_EXIST);
        }
        //snowflake id
        long id = ProjectSnowflakeIdWorker.getInstance().nextId();
        //insert account to db
        account = new Account(id, username, passwordEncoder.encode(oauthAccount.getPassword()));
        AssertUtil.isTrue(accountTkService.insert(account), CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message);
        //insert oauthClient to db
        AccountOauthClient accountOauthClient = new AccountOauthClient(id, username);
        AssertUtil.isTrue(accountOauthClientTkService.insert(accountOauthClient), CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message);

        return CommonResultCode.messageResponse();
    }
}
