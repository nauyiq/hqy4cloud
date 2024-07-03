package com.hqy.cloud.collection.core.exception;

import com.hqy.cloud.coll.service.ExceptionCollectionService;
import com.hqy.cloud.coll.struct.PfExceptionStruct;
import com.hqy.cloud.collection.api.AbstractCollector;
import com.hqy.cloud.collection.common.BusinessCollectionType;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常采集器, 调用采集服务RPC
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 16:32
 */
@Slf4j
public class ExceptionCollector extends AbstractCollector<PfExceptionStruct> {
    public ExceptionCollector(ExceptionCollectionConfigProperties config) {
        super(config);
    }

    @Override
    public BusinessCollectionType type() {
        return BusinessCollectionType.EXCEPTION;
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
