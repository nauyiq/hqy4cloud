package com.hqy.cloud.auth.service.tk.support;

import com.hqy.cloud.auth.mapper.RoleResourcesMapper;
import com.hqy.cloud.auth.base.dto.AuthenticationDTO;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.entity.RoleResources;
import com.hqy.cloud.auth.service.tk.RoleResourcesTkService;
import com.hqy.cloud.tk.PrimaryLessTkMapper;
import com.hqy.cloud.tk.support.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 10:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleResourcesTkServiceImpl extends PrimaryLessTkServiceImpl<RoleResources> implements RoleResourcesTkService {

    private final RoleResourcesMapper dao;

    @Override
    public PrimaryLessTkMapper<RoleResources> getTkDao() {
        return dao;
    }


    @Override
    public boolean insertOrUpdateRoleResources(Integer roleId, String role, List<ResourceDTO> resources) {
        if (role == null || StringUtils.isBlank(role) || CollectionUtils.isEmpty(resources)) {
            log.warn("Failed execute to insertOrUpdateRoleResources, params: {}, {}, {}", roleId, role, resources);
            return false;
        }
        return dao.insertOrUpdateRoleResources(roleId, role, resources) > 0;
    }

    @Override
    public Map<String, List<ResourceDTO>> getAuthoritiesResourcesByRoles(List<String> roles) {
        return dao.getAuthoritiesResourcesByRoles(roles);
    }

    @Override
    public boolean deleteByRoleAndResourceIds(Integer roleId, List<Integer> resourceIds) {
        return dao.deleteByRoleAndResourceIds(roleId, resourceIds) > 0;
    }

    @Override
    public boolean deleteByResourceIdAndRoleIds(Integer resourceId, List<Integer> roleIds) {
        Example example = new Example(RoleResources.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("resourceId", resourceId);
        criteria.andIn("roleId", roleIds);
        return dao.deleteByExample(example) > 0;
    }
}
