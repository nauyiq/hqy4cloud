package com.hqy.cloud.thrift.sentinel.adaptor;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hqy.cloud.rpc.core.RPCContext;
import com.hqy.cloud.rpc.thrift.service.ThriftContextClientHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.hqy.cloud.thrift.sentinel.adaptor.Constants.SENTINEL_THRIFT_INTERFACE_RESOURCE_ENTRY;
import static com.hqy.cloud.thrift.sentinel.adaptor.Constants.SENTINEL_THRIFT_METHOD_ENTRY;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/5 14:51
 */
@Slf4j
public class SentinelThriftContextClientHandleService implements ThriftContextClientHandleService {

    @Override
    @SneakyThrows
    public void doPreWrite(ThriftContext thriftContext, String methodName, Object[] args)  {
        Entry interfaceEntry = null;
        Entry methodEntry = null;
        RPCContext rpcContext = thriftContext.getRpcContext();
        if (Objects.isNull(rpcContext)) {
            log.warn("Rpc context should not be null.");
            return;
        }
        String interfaceResourceName = rpcContext.getServiceClass().getName();
        try {
            interfaceEntry = SphU.entry(interfaceResourceName, ResourceTypeConstants.COMMON_RPC, EntryType.OUT);
            methodEntry = SphU.entry(methodName, ResourceTypeConstants.COMMON_RPC, EntryType.OUT, rpcContext.getArguments());
        } catch (BlockException e) {
            log.info("Thrift rpc by sentinel blocked.");
            throw e;
        } finally {
            thriftContext.setAttachment(SENTINEL_THRIFT_INTERFACE_RESOURCE_ENTRY, interfaceEntry);
            thriftContext.setAttachment(SENTINEL_THRIFT_METHOD_ENTRY, methodEntry);
        }
    }

    @Override
    public void doDone(ThriftContext thriftContext, String methodName) {
        RPCContext rpcContext = thriftContext.getRpcContext();
        if (Objects.isNull(rpcContext)) {
            return;
        }
        Entry interfaceEntry = (Entry) thriftContext.getAttachment(SENTINEL_THRIFT_INTERFACE_RESOURCE_ENTRY);
        Entry methodEntry = (Entry) thriftContext.getAttachment(SENTINEL_THRIFT_METHOD_ENTRY);
        if (Objects.isNull(interfaceEntry) && Objects.isNull(methodEntry)) {
            log.warn("interfaceEntry and methodEntry should not be null.");
            return;
        }
        try {
            Throwable exception = thriftContext.getException();
            if (Objects.nonNull(exception) && !thriftContext.isResult()) {
                Tracer.traceEntry(exception, interfaceEntry);
                Tracer.traceEntry(exception, methodEntry);
            }
        } finally {
            if (Objects.nonNull(methodEntry)) {
                methodEntry.exit(1, rpcContext.getArguments());
            }
            if (Objects.nonNull(interfaceEntry)) {
                interfaceEntry.exit();
            }
        }


    }

    @Override
    public boolean isThrowException() {
        return true;
    }
}
