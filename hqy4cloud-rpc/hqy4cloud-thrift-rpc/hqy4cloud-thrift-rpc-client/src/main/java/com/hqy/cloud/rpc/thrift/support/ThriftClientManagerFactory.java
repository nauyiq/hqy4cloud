package com.hqy.cloud.rpc.thrift.support;

import com.facebook.nifty.client.NettyClientConfig;
import com.facebook.nifty.client.NiftyClient;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftClientManager;
import com.hqy.cloud.thrift.client.ThriftContextClientEventHandler;
import com.hqy.cloud.rpc.thrift.service.ThriftContextClientHandleService;

import java.util.Collections;
import java.util.List;

/**
 * create ThriftClientManagerWrapper.
 * @see ThriftClientManagerWrapper
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 17:25
 */
public class ThriftClientManagerFactory {

    public ThriftClientManagerWrapper createThriftClientManager(int workerThreadCount, List<ThriftContextClientHandleService> services) {
        ThriftCodecManager codecManager = new ThriftCodecManager();
        NettyClientConfig config = NettyClientConfig.newBuilder().setWorkerThreadCount(workerThreadCount).build();
        NiftyClient client = new NiftyClient(config);
        ThriftClientManager thriftClientManager = new ThriftClientManager(codecManager, client, Collections.singleton(new ThriftContextClientEventHandler(services)));
        return new ThriftClientManagerWrapper(thriftClientManager);
    }


}
