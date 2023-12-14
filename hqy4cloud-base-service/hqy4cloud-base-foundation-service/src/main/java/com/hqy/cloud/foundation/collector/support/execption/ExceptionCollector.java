package com.hqy.cloud.foundation.collector.support.execption;

import com.hqy.cloud.coll.service.ExceptionCollectionService;
import com.hqy.cloud.coll.struct.PfExceptionStruct;
import com.hqy.cloud.foundation.collector.AbstractCollector;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.foundation.collection.CollectionType;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常采集器, 调用采集服务RPC
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 16:32
 */
@Slf4j
public class ExceptionCollector extends AbstractCollector<PfExceptionStruct> {

    @Override
    public CollectionType type() {
        return CollectionType.EXCEPTION;
    }

    @Override
    protected void doCollect(PfExceptionStruct struct) {
        if (struct == null) {
            return;
        }
        ExceptionCollectionService exceptionCollectionService = RPCClient.getRemoteService(ExceptionCollectionService.class);
        exceptionCollectionService.collect(struct);
    }
}
