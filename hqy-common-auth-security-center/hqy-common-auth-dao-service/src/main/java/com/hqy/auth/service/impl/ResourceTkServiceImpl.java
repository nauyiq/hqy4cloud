package com.hqy.auth.service.impl;

import com.hqy.auth.dao.ResourceDao;
import com.hqy.auth.entity.Resource;
import com.hqy.auth.service.ResourceTkService;
import com.hqy.account.struct.ResourcesInRoleStruct;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 18:00
 */
@Service
@RequiredArgsConstructor
public class ResourceTkServiceImpl extends BaseTkServiceImpl<Resource, Integer> implements ResourceTkService {

    private final ResourceDao resourceDao;

    @Override
    public BaseDao<Resource, Integer> getTkDao() {
        return resourceDao;
    }

    @Override
    public List<ResourcesInRoleStruct> getResourcesByRoles(List<String> roles) {
        return resourceDao.getResourcesByRoles(roles);
    }
}
