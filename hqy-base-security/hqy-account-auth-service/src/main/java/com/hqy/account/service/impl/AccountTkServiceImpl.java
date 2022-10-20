package com.hqy.account.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.account.dao.AccountDao;
import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.entity.Account;
import com.hqy.account.service.AccountTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.util.AssertUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
public class AccountTkServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountTkService {


    @Resource
    private AccountDao accountDao;

    @Override
    public BaseDao<Account, Long> selectDao() {
        return accountDao;
    }


    @Override
    public Account queryAccountByUsernameOrEmail(String usernameOrEmail) {
        return accountDao.queryAccountByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        return accountDao.getAccountInfo(id);
    }

    @Override
    public List<AccountInfoDTO> getAccountInfos(List<Long> ids) {
        return accountDao.getAccountInfos(ids);
    }
}
