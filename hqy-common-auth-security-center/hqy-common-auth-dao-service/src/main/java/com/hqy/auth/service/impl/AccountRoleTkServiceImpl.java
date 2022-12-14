package com.hqy.auth.service.impl;

import com.hqy.auth.dao.AccountRoleDao;
import com.hqy.auth.entity.AccountRole;
import com.hqy.auth.service.AccountRoleTkService;
import com.hqy.base.PrimaryLessTkDao;
import com.hqy.base.impl.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:30
 */
@Service
@RequiredArgsConstructor
public class AccountRoleTkServiceImpl extends PrimaryLessTkServiceImpl<AccountRole> implements AccountRoleTkService {

    private final AccountRoleDao accountRoleDao;

    @Override
    public PrimaryLessTkDao<AccountRole> getTkDao() {
        return accountRoleDao;
    }

}
