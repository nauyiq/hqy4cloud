package com.hqy.cloud.foundation.event.collector.support.execption;

import com.hqy.cloud.coll.service.ExceptionCollectionService;
import com.hqy.cloud.coll.struct.PfExceptionStruct;
import com.hqy.cloud.foundation.event.collector.AbstractCollector;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.foundation.common.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常采集器, 调用采集服务RPC
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 16:32
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionCollector extends AbstractCollector<PfExceptionStruct> {

    @Override
    public EventType type() {
        return EventType.EXCEPTION;
    }

    @Override
    protected void doCollect(PfExceptionStruct struct) {
        if (struct == null) {
            return;
        }
        ExceptionCollectionService exceptionCollectionService = RpcClient.getRemoteService(ExceptionCollectionService.class);
        exceptionCollectionService.collect(struct);
    }

}
