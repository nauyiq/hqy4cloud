package com.hqy.account.dao;

import com.hqy.account.entity.OauthClient;
import com.hqy.base.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:51
 */
@Repository
public interface OauthClientDao extends BaseDao<OauthClient, String> {
}
