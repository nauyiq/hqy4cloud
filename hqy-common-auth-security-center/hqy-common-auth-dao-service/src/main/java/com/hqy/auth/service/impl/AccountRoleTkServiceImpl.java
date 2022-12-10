package com.hqy.auth.service.impl;

import com.hqy.auth.dao.AccountRoleDao;
import com.hqy.auth.entity.AccountRole;
import com.hqy.auth.service.AccountRoleTkService;
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
