package com.hqy.cloud.thrift.sentinel.adaptor;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerContext;
import com.hqy.cloud.thrift.sentinel.adaptor.exception.ThriftSentinelBlockException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.hqy.cloud.thrift.sentinel.adaptor.Constants.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 13:42
 */
@Slf4j
public class SentinelThriftServerContextHandleServiceImpl implements ThriftServerContextHandleService {

    @Override
    @SneakyThrows
    public void doPreInvokeMethod(ThriftServerContext thriftServerContext, String methodName, Object[] args) {
        if (CommonSwitcher.ENABLE_RPC_SENTINEL_ADAPTOR_HANDLER.isOn()) {
            RpcModel rpcModel = thriftServerContext.getRpcModel();
            if (Objects.isNull(rpcModel)) {
                log.warn("Rpc context should not be null.");
                return;
            }
            String origin = rpcModel.getName();
            Entry methodEntry = null;
            Entry interfaceEntry = null;
            try {
                // Only need to create entrance context at provider side, as context will take effect
                // at entrance of invocation chain only (for inbound traffic).
                methodName = thriftServerContext.getMethodName();
                ContextUtil.enter(methodName, origin);
                interfaceEntry = SphU.entry(thriftServerContext.getServiceTypeName(), ResourceTypeConstants.COMMON_RPC, EntryType.IN,
                        args);
                methodEntry = SphU.entry(methodName, ResourceTypeConstants.COMMON_RPC, EntryType.IN,
                        args);
            } catch (BlockException e) {
                log.info("Thrift rpc by sentinel blocked.");
                throw new ThriftSentinelBlockException(ThriftSentinelBlockException.ID, e);
            } finally {
                thriftServerContext.setAttachment(SENTINEL_THRIFT_METHOD_ENTRY, methodEntry);
                thriftServerContext.setAttachment(SENTINEL_THRIFT_INTERFACE_RESOURCE_ENTRY, interfaceEntry);
                thriftServerContext.setAttachment(THRIFT_SERVER_HANDLER_REQUEST_ARGS, args);
            }
        }
    }


    @Override
    public void doDone(ThriftServerContext thriftServerContext, String methodName) {
        if (CommonSwitcher.ENABLE_RPC_SENTINEL_ADAPTOR_HANDLER.isOn()) {
            Entry methodEntry = (Entry) thriftServerContext.getAttachment(SENTINEL_THRIFT_METHOD_ENTRY);
            Entry interfaceEntry = (Entry) thriftServerContext.getAttachment(SENTINEL_THRIFT_INTERFACE_RESOURCE_ENTRY);
            if (Objects.isNull(methodEntry) && Objects.isNull(interfaceEntry)) {
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
                    Tracer.traceEntry(exception, interfaceEntry);
                }
            } finally {
                if (Objects.nonNull(methodEntry)) {
                    methodEntry.exit(1, args);
                }
                if (Objects.nonNull(interfaceEntry)) {
                    interfaceEntry.exit();
                }
            }
        }
    }

    @Override
    public boolean isThrowException() {
        return true;
    }
}
