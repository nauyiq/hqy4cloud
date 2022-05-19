package com.hqy.account.service.impl;

import com.hqy.account.dao.OauthClientDao;
import com.hqy.account.entity.OauthClient;
import com.hqy.account.service.OauthClientService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:53
 */
@Service
public class OauthClientServiceImpl extends BaseTkServiceImpl<OauthClient, String> implements OauthClientService {

    @Resource
    private OauthClientDao oauthClientDao;

    @Override
    public BaseDao<OauthClient, String> selectDao() {
        return oauthClientDao;
    }
}
