package com.hqy.cloud.collection.core.throttles;

import com.hqy.cloud.coll.service.CollPersistService;
import com.hqy.cloud.coll.struct.ThrottledBlockStruct;
import com.hqy.cloud.collection.api.AbstractCollector;
import com.hqy.cloud.collection.common.BusinessCollectionType;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 节流器数据采集
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 17:22
 */
@Slf4j
public class ThrottledCollector extends AbstractCollector<ThrottledBlockStruct> {

    public ThrottledCollector(ThrottleCollectionConfigProperties config) {
        super(config);
    }

    @Override
    public BusinessCollectionType type() {
        return BusinessCollectionType.THROTTLES;
    }

    @Override
    protected void doCollect(ThrottledBlockStruct struct) {
        CollPersistService remoteService = RpcClient.getRemoteService(CollPersistService.class);
        remoteService.saveThrottledBlockHistory(struct);
    }
}
