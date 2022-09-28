package com.hqy.account.service.impl;

import com.hqy.account.dao.AccountRoleDao;
import com.hqy.account.entity.AccountRole;
import com.hqy.account.service.AccountRoleTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
public class AccountRoleTkServiceImpl extends BaseTkServiceImpl<AccountRole, Long> implements AccountRoleTkService {

    @Resource
    private AccountRoleDao accountRoleDao;

    @Override
    public BaseDao<AccountRole, Long> selectDao() {
        return accountRoleDao;
    }
}
