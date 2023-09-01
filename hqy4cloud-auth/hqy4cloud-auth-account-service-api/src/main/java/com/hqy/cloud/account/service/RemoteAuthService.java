package com.hqy.cloud.account.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.account.struct.AuthenticationStruct;
import com.hqy.cloud.account.struct.ResourceStruct;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.service.RPCService;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 13:48
 */
@ThriftService(MicroServiceConstants.ACCOUNT_SERVICE)
public interface RemoteAuthService extends RPCService {

    /**
     * 根据角色获取可以访问的资源数据.
     * @param  roles 角色列表.
     * @return       ResourcesInRoleStruct.
     */
    @ThriftMethod
    List<AuthenticationStruct> getAuthoritiesResourcesByRoles(@ThriftField(1)List<String> roles);

    /**
     * 根据角色获取接口permissions
     * @param roles 角色列表
     * @return      permissions
     */
    @ThriftMethod
    List<String> getPermissionsByRoles(@ThriftField(1)List<String> roles);


    /**
     * 根据角色更新资源信息
     * @param role             角色
     * @param resourceStructs  资源
     */
    @ThriftMethod(oneway = true)
    void updateAuthoritiesResource(@ThriftField(1)String role, @ThriftField(2)List<ResourceStruct> resourceStructs);


}
