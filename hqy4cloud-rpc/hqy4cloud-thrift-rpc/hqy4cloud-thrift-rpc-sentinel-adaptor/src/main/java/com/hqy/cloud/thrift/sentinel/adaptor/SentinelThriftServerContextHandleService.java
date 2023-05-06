package com.hqy.cloud.thrift.sentinel.adaptor;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.hqy.cloud.thrift.sentinel.adaptor.Constants.SENTINEL_THRIFT_METHOD_ENTRY;
import static com.hqy.cloud.thrift.sentinel.adaptor.Constants.THRIFT_SERVER_HANDLER_REQUEST_ARGS;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 13:42
 */
@Slf4j
public class SentinelThriftServerContextHandleService implements ThriftServerContextHandleService {

    @Override
    @SneakyThrows
    public void doPostRead(ThriftServerContext thriftServerContext, String methodName, Object[] args) {
        RPCModel rpcModel = thriftServerContext.getRpcModel();
        if (Objects.isNull(rpcModel)) {
            log.warn("Rpc context should not be null.");
            return;
        }
        String origin = rpcModel.getName();
        Entry methodEntry = null;
        try {
            // Only need to create entrance context at provider side, as context will take effect
            // at entrance of invocation chain only (for inbound traffic).
            ContextUtil.enter(methodName, origin);
            methodEntry = SphU.entry(methodName, ResourceTypeConstants.COMMON_RPC, EntryType.IN,
                    args);
        } catch (BlockException e) {
            log.info("Thrift rpc by sentinel blocked.");
            throw e;
        } finally {
            thriftServerContext.setAttachment(SENTINEL_THRIFT_METHOD_ENTRY, methodEntry);
            thriftServerContext.setAttachment(THRIFT_SERVER_HANDLER_REQUEST_ARGS, args);
        }
    }


    @Override
    public void doDone(ThriftServerContext thriftServerContext, String methodName) {
        Entry methodEntry = (Entry) thriftServerContext.getAttachment(SENTINEL_THRIFT_METHOD_ENTRY);
        if (Objects.isNull(methodEntry)) {
            log.warn("methodEntry should not be null.");
            return;
        }
        Object[] args;
        Object attachment = thriftServerContext.getAttachment(THRIFT_SERVER_HANDLER_REQUEST_ARGS);
        if (Objects.isNull(attachment)) {
            args = null;
        } else {
            args = (Object[]) attachment;
        }
        try {
            Throwable exception = thriftServerContext.getException();
            if (Objects.nonNull(exception) && !thriftServerContext.isResult()) {
                Tracer.traceEntry(exception, methodEntry);
            }
        } finally {
            methodEntry.exit(1, args);
        }


    }
}
