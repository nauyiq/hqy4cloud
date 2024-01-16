package com.hqy.cloud.foundation.event.collector.support.throttles;

import com.hqy.cloud.coll.service.CollPersistService;
import com.hqy.cloud.coll.struct.ThrottledBlockStruct;
import com.hqy.cloud.foundation.event.collector.AbstractCollector;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.foundation.common.EventType;
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
    public EventType type() {
        return EventType.THROTTLES;
    }

    @Override
    protected void doCollect(ThrottledBlockStruct struct) {
        CollPersistService remoteService = RpcClient.getRemoteService(CollPersistService.class);
        remoteService.saveThrottledBlockHistory(struct);
    }
}
