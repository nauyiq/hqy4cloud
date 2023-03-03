package com.hqy.cloud.auth.service;

import com.hqy.account.service.RemoteAuthService;
import com.hqy.account.struct.AuthenticationStruct;
import com.hqy.account.struct.ResourceStruct;
import com.hqy.cloud.auth.base.dto.AuthenticationDTO;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.tk.RoleResourcesTkService;
import com.hqy.cloud.auth.service.tk.RoleTkService;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 13:50
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteAuthServiceImpl extends AbstractRPCService implements RemoteAuthService {
    private final AccountAuthOperationService accountAuthOperationService;
    private final RoleTkService roleTkService;
    private final RoleResourcesTkService roleResourcesTkService;

    @Override
    public List<AuthenticationStruct> getAuthoritiesResourcesByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        List<AuthenticationDTO> authentications = accountAuthOperationService.getAuthoritiesResourcesByRoles(roles);
        if (CollectionUtils.isEmpty(authentications)) {
            return Collections.emptyList();
        }
        return authentications.stream().map(this::convert).collect(Collectors.toList());
    }

    private AuthenticationStruct convert(AuthenticationDTO dto) {
        AuthenticationStruct struct = new AuthenticationStruct();
        struct.role = dto.getRole();
        List<ResourceDTO> resources = dto.getResources();
        if (CollectionUtils.isEmpty(resources)) {
            struct.resources = Collections.emptyList();
        } else {
            struct.resources = resources.stream().map(e -> new ResourceStruct(e.getId(), e.getPath(), e.getMethod(), e.getPermission())).collect(Collectors.toList());
        }
        return struct;
    }

    @Override
    public void updateAuthoritiesResource(String role, List<ResourceStruct> resourceStructs) {
        if (StringUtils.isBlank(role) || CollectionUtils.isEmpty(resourceStructs)) {
            log.warn("Role or resourceStructs should not be empty.");
            return;
        }
        //获取对应role数据。
        Role accountRole = roleTkService.queryOne(new Role(role));
        AssertUtil.notNull(accountRole, "Not found role name: " + role);
        List<ResourceDTO> resources = resourceStructs.stream().map(e -> new ResourceDTO()).collect(Collectors.toList());
        roleResourcesTkService.insertOrUpdateRoleResources(accountRole.getId(), role, resources);

    }
}
