package com.hqy.cloud.auth.api.support;

import com.hqy.cloud.account.service.RemoteAuthService;
import com.hqy.cloud.account.struct.AuthenticationStruct;
import com.hqy.cloud.account.struct.ResourceStruct;
import com.hqy.cloud.auth.api.AuthoritiesRoleService;
import com.hqy.cloud.auth.common.AuthenticationModuleInfo;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于账号服务获取账号权限。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
public class RemoteAccountAuthoritiesRoleService implements AuthoritiesRoleService {

    @Override
    public List<AuthenticationModuleInfo> loadAuthenticationModulesByAuthorities(List<String> authorities) {
        RemoteAuthService remoteAuthService = RpcClient.getRemoteService(RemoteAuthService.class);
        List<AuthenticationStruct> structs = remoteAuthService.getAuthoritiesResourcesByRoles(authorities);
        if (CollectionUtils.isEmpty(structs)) {
            return List.of();
        }
        return convertToResult(structs);
    }

    @Override
    public Set<String> loadAuthenticationPermissionsByAuthorities(List<String> authorities) {
        RemoteAuthService remoteAuthService = RpcClient.getRemoteService(RemoteAuthService.class);
        List<String> permissions = remoteAuthService.getPermissionsByRoles(authorities);
        return new HashSet<>(permissions);
    }


    private List<AuthenticationModuleInfo> convertToResult(List<AuthenticationStruct> structs) {
        return structs.stream().map(struct -> {
            List<ResourceStruct> resources = struct.getResources();
            List<AuthenticationModuleInfo.ModuleInfo> moduleInfos = CollectionUtils.isEmpty(resources) ? List.of() :
                    resources.stream().map(resource -> new AuthenticationModuleInfo.ModuleInfo(resource.path, resource.method)).toList();
            return new AuthenticationModuleInfo(struct.role, moduleInfos);
        }).toList();
    }
}
