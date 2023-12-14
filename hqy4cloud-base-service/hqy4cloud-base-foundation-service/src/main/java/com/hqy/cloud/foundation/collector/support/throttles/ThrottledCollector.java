package com.hqy.cloud.foundation.collector.support.throttles;

import com.hqy.cloud.coll.service.CollPersistService;
import com.hqy.cloud.coll.struct.ThrottledBlockStruct;
import com.hqy.cloud.foundation.collector.AbstractCollector;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.foundation.collection.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 节流器数据采集
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 17:22
 */
@Slf4j
@Component
public class ThrottledCollector extends AbstractCollector<ThrottledBlockStruct> {

    @Override
    public CollectionType type() {
        return CollectionType.THROTTLES;
    }

    @Override
    protected void doCollect(ThrottledBlockStruct struct) {
        CollPersistService remoteService = RPCClient.getRemoteService(CollPersistService.class);
        remoteService.saveThrottledBlockHistory(struct);
    }
}
