package com.hqy.cloud.auth.service.tk.support;

import com.hqy.cloud.auth.mapper.AccountProfileTkMapper;
import com.hqy.cloud.auth.entity.AccountProfile;
import com.hqy.cloud.auth.service.tk.AccountProfileTkService;
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
    public BaseTkMapper<AccountProfile, Long> getTkMapper() {
        return accountProfileDao;
    }
}
