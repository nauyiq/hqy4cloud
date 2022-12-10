package com.hqy.auth.service.impl;

import com.hqy.auth.dao.AccountDao;
import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.auth.entity.Account;
import com.hqy.auth.service.AccountTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.util.AssertUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
public class AccountTkServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountTkService {


    @Resource
    private AccountDao accountDao;

    @Override
    public BaseDao<Account, Long> getTkDao() {
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
