package com.hqy.account.service.impl;

import com.hqy.account.dao.RoleResourcesDao;
import com.hqy.account.entity.RoleResources;
import com.hqy.account.service.RoleResourcesTkService;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.base.PrimaryLessTkDao;
import com.hqy.base.impl.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 10:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleResourcesTkServiceImpl extends PrimaryLessTkServiceImpl<RoleResources> implements RoleResourcesTkService {

    private final RoleResourcesDao dao;

    @Override
    public PrimaryLessTkDao<RoleResources> getTkDao() {
        return dao;
    }


    @Override
    public void insertOrUpdateRoleResources(Integer roleId, String role, List<ResourceStruct> resourceStructs) {
        if (role == null || StringUtils.isBlank(role) || CollectionUtils.isEmpty(resourceStructs)) {
            log.warn("Failed execute to insertOrUpdateRoleResources, params: {}, {}, {}", roleId, role, resourceStructs);
            return;
        }
        dao.insertOrUpdateRoleResources(roleId, role, resourceStructs);
    }
}
