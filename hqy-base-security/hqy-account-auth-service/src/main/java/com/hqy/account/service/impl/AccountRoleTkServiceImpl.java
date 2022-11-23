package com.hqy.account.service.impl;

import com.hqy.account.dao.AccountRoleDao;
import com.hqy.account.entity.AccountRole;
import com.hqy.account.service.AccountRoleTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
@RequiredArgsConstructor
public class AccountRoleTkServiceImpl extends BaseTkServiceImpl<AccountRole, Integer> implements AccountRoleTkService {

    private final AccountRoleDao accountRoleDao;

    @Override
    public BaseDao<AccountRole, Integer> getTkDao() {
        return accountRoleDao;
    }
}
