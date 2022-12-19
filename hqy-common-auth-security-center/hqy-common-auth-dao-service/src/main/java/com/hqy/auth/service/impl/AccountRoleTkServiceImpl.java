package com.hqy.auth.service.impl;

import com.hqy.auth.dao.AccountRoleDao;
import com.hqy.auth.entity.AccountRole;
import com.hqy.auth.service.AccountRoleTkService;
import com.hqy.base.PrimaryLessTkDao;
import com.hqy.base.impl.PrimaryLessTkServiceImpl;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public boolean updateRoleLevel(Integer roleId, Integer level) {
        return accountRoleDao.updateRoleLevel(roleId, level) > 0;
    }

    @Override
    public boolean deleteByAccountRoles(List<AccountRole> accountRoles) {
        AssertUtil.notEmpty(accountRoles, "Account roles should not be null.");
        Example example = new Example(AccountRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("accountId", accountRoles.stream().map(AccountRole::getAccountId).collect(Collectors.toList()))
                .andIn("roleId", accountRoles.stream().map(AccountRole::getRoleId).collect(Collectors.toList()));
        return accountRoleDao.deleteByExample(example) > 0;
    }
}
