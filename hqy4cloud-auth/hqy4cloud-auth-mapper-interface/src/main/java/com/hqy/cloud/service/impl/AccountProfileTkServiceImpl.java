package com.hqy.cloud.service.impl;

import com.hqy.cloud.mapper.AccountProfileTkMapper;
import com.hqy.cloud.entity.AccountProfile;
import com.hqy.cloud.service.AccountProfileTkService;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:40
 */
@Service
@RequiredArgsConstructor
public class AccountProfileTkServiceImpl extends BaseTkServiceImpl<AccountProfile, Long> implements AccountProfileTkService {

    private final AccountProfileTkMapper accountProfileDao;

    @Override
    public BaseTkMapper<AccountProfile, Long> getTkDao() {
        return accountProfileDao;
    }
}
