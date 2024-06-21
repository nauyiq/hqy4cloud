package com.hqy.cloud.auth.account.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.dto.RoleOnResourcesDTO;
import com.hqy.cloud.auth.account.entity.RoleResources;
import com.hqy.cloud.auth.account.mapper.RoleResourcesMapper;
import com.hqy.cloud.auth.account.service.RoleResourcesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 10:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleResourcesServiceImpl extends ServiceImpl<RoleResourcesMapper, RoleResources> implements RoleResourcesService {
    private final RoleResourcesMapper mapper;


    @Override
    public boolean insertOrUpdateRoleResources(Integer roleId, String role, List<ResourceDTO> resources) {
        if (role == null || StringUtils.isBlank(role) || CollectionUtils.isEmpty(resources)) {
            log.warn("Failed execute to insertOrUpdateRoleResources, params: {}, {}, {}", roleId, role, resources);
            return false;
        }
        return mapper.insertOrUpdateRoleResources(roleId, role, resources) > 0;
    }

    @Override
    public List<String> getRolesByResource(Integer resourceId) {
        return mapper.getRolesByResource(resourceId);
    }

    @Override
    public Map<String, List<ResourceDTO>> getAuthoritiesResourcesByRoles(List<String> roles) {
        List<RoleOnResourcesDTO> authoritiesResourcesByRoles = mapper.getAuthoritiesResourcesByRoles(roles);
        if (CollectionUtils.isEmpty(authoritiesResourcesByRoles)) {
            return MapUtil.empty();
        }
        return authoritiesResourcesByRoles.stream().collect(Collectors.toMap(RoleOnResourcesDTO::getRoleName, RoleOnResourcesDTO::getResources));
    }

    @Override
    public boolean deleteByRoleAndResourceIds(Integer roleId, List<Integer> resourceIds) {
        return mapper.deleteByRoleAndResourceIds(roleId, resourceIds) > 0;
    }

    @Override
    public boolean deleteByResourceIdAndRoleIds(Integer resourceId, List<Integer> roleIds) {
        Example example = new Example(RoleResources.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("resourceId", resourceId);
        criteria.andIn("roleId", roleIds);
        return mapper.deleteByExample(example) > 0;
    }
}
