package com.hqy.coll.service;


import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.coll.struct.PageThrottledBlockResultStruct;
import com.hqy.coll.struct.ThrottledBlockStruct;
import com.hqy.rpc.api.service.RPCService;
import com.hqy.rpc.thrift.struct.PageStruct;

/**
 * 采集服务RPC接口
 * @author qy
 * @date 2021-08-10 15:23
 */
@ThriftService(MicroServiceConstants.COMMON_COLLECTOR)
public interface CollPersistService extends RPCService {

    /**
     * 保存一条网关封禁的ip记录到数据库
     * @param struct thrift rpc struct
     */
    @ThriftMethod(oneway = true)
    void saveThrottledBlockHistory(@ThriftField(1) ThrottledBlockStruct struct);

    /**
     * 删除一条网关封禁记录
     * @param id ThrottledBlock ID
     */
    @ThriftMethod(oneway = true)
    void deleteThrottledBlockHistory(@ThriftField(1) Long id);

    /**
     * 分页查询节流封禁数据
     * @param throttleBy 模糊查询-节流方式
     * @param ip         模糊查询-ip
     * @param uri        模糊查询-uri
     * @param struct     分页参数
     * @return           {@link PageThrottledBlockResultStruct}
     */
    @ThriftMethod
    PageThrottledBlockResultStruct getPageThrottledBlock(@ThriftField(1)String throttleBy,
                                                         @ThriftField(2)String ip, @ThriftField(3)String uri, @ThriftField(4)PageStruct struct);


}
