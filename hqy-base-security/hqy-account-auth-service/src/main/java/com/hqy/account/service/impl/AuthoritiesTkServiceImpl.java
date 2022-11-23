package com.hqy.account.service.impl;

import com.hqy.account.dao.AuthoritiesDao;
import com.hqy.account.entity.Authorities;
import com.hqy.account.service.AuthoritiesTkService;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 9:03
 */
@Service
@RequiredArgsConstructor
public class AuthoritiesTkServiceImpl extends BaseTkServiceImpl<Authorities, Integer> implements AuthoritiesTkService {

    private final AuthoritiesDao authoritiesDao;

    @Override
    public BaseDao<Authorities, Integer> getTkDao() {
        return authoritiesDao;
    }


    @Override
    public void insertOrUpdateAuthoritiesResource(Integer roleId, String role, List<ResourceStruct> resourceStructs) {
        authoritiesDao.insertOrUpdateAuthoritiesResource(roleId, role, resourceStructs, new Date());
    }
}
