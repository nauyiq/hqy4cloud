package com.hqy.cloud.auth.service;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.account.service.RemoteAuthService;
import com.hqy.cloud.account.struct.AuthenticationStruct;
import com.hqy.cloud.account.struct.ResourceStruct;
import com.hqy.cloud.auth.account.service.RoleResourcesService;
import com.hqy.cloud.auth.account.service.RoleService;
import com.hqy.cloud.auth.account.service.SysOauthClientService;
import com.hqy.cloud.auth.base.dto.AuthenticationDTO;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.account.entity.SysOauthClient;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private final AccountOperationService accountOperationService;
    private final AuthOperationService authOperationService;
    private final RoleService roleService;
    private final RoleResourcesService roleResourcesService;
    private final SysOauthClientService sysOauthClientService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<AuthenticationStruct> getAuthoritiesResourcesByRoles(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        Map<String, List<String>> permissionsMap = authOperationService.getPermissionsByRoles(roles);
        permissionsMap = Objects.isNull(permissionsMap) ? MapUtil.empty() : permissionsMap;
        Map<String, List<ResourceDTO>> authoritiesMap = authOperationService.getAuthoritiesResourcesByRoles(roles);
        authoritiesMap = Objects.isNull(authoritiesMap) ? MapUtil.empty() : authoritiesMap;
        List<AuthenticationStruct> structs = new ArrayList<>(roles.size());
        for (String role : roles) {
            List<String> permissions = permissionsMap.get(role);
            List<ResourceDTO> resources = authoritiesMap.get(role);
            permissions = CollectionUtils.isEmpty(permissions) ? Collections.emptyList() : permissions;
            resources = CollectionUtils.isEmpty(resources) ? Collections.emptyList() : resources;
            List<ResourceStruct> resourceStructs = resources.stream()
                    .map(resource -> new ResourceStruct(resource.getId(), resource.getPath(), resource.getMethod())).collect(Collectors.toList());
            structs.add(new AuthenticationStruct(role, resourceStructs, permissions));
        }
        return structs;
    }

    @Override
    public List<String> getPermissionsByRoles(List<String> roles) {
        return authOperationService.getMenuPermissionsByRoles(roles);
    }


    private AuthenticationStruct convert(AuthenticationDTO dto) {
        AuthenticationStruct struct = new AuthenticationStruct();
        struct.role = dto.getRole();
        List<ResourceDTO> resources = dto.getResources();
        if (CollectionUtils.isEmpty(resources)) {
            struct.resources = Collections.emptyList();
        } else {
            struct.resources = resources.stream().map(e -> new ResourceStruct(e.getId(), e.getPath(), e.getMethod())).collect(Collectors.toList());
        }
        return struct;
    }


    @Override
    public CommonResultStruct basicAuth(String clientId, String clientSecret) {
        if (StringUtils.isAnyBlank(clientId, clientSecret)) {
            return CommonResultStruct.of(ResultCode.INVALID_CLIENT_OR_SECRET);
        }
        SysOauthClient oauthClient = sysOauthClientService.queryById(clientId);
        if (oauthClient == null
                || !clientId.equals(oauthClient.getClientId())
                || passwordEncoder.matches(clientSecret, oauthClient.getClientSecret())) {
            return CommonResultStruct.of(ResultCode.INVALID_CLIENT_OR_SECRET);
        }
        return CommonResultStruct.of();
    }
}
