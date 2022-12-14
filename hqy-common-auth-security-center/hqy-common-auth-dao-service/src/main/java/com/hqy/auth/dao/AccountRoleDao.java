package com.hqy.auth.dao;

import com.hqy.auth.entity.AccountRole;
import com.hqy.base.PrimaryLessTkDao;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:27
 */
@Repository
public interface AccountRoleDao extends PrimaryLessTkDao<AccountRole> {
}
