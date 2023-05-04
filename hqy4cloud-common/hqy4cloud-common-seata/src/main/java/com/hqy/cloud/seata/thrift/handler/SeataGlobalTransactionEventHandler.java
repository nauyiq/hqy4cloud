package com.hqy.cloud.seata.thrift.handler;

import com.facebook.ThriftRequestPram;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.core.RPCContext;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftServerContext;
import io.seata.core.context.RootContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SeataGlobalTransactionEventHandler.
 * @see RootContext
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 16:33
 */
public class SeataGlobalTransactionEventHandler implements ThriftServerContextHandleService {
    private static final Logger log = LoggerFactory.getLogger(SeataGlobalTransactionEventHandler.class);

    @Override
    public void doPostRead(ThriftServerContext thriftServerContext, String methodName, Object[] args) {
        ThriftServerContextHandleService.super.doPostRead(thriftServerContext, methodName, args);
        boolean isGlobalTransactionalMethod = RPCContext.isGlobalTransactionalMethod(methodName);
        if (!isGlobalTransactionalMethod) {
            if (log.isDebugEnabled()) {
                log.debug("Rpc method:{} not support global transaction.", methodName);
            }
            return;
        }
        ThriftRequestPram requestPram = thriftServerContext.getRequestPram();
        if (requestPram == null) {
            if (log.isDebugEnabled()) {
                log.debug("Rpc method:{} request pram is empty.", methodName);
            }
            return;
        }
        String rpcXid = requestPram.getParameter(CommonConstants.SEATA_XID);
        if (StringUtils.isBlank(rpcXid)) {
            if (log.isDebugEnabled()) {
                log.debug("Ignore inject xid, rpcXid is empty. method:{}.", methodName);
            }
            return;
        }
        String xid = RootContext.getXID();
        if (StringUtils.isBlank(xid)) {
            RootContext.bind(rpcXid);
            thriftServerContext.setBind(true);
        }
    }


    @Override
    public void doDone(ThriftServerContext thriftServerContext, String methodName) {
        ThriftServerContextHandleService.super.doDone(thriftServerContext, methodName);

        try {
            if (thriftServerContext.isBind()) {
                String rpcXid = thriftServerContext.getRequestPram().getParameter(CommonConstants.SEATA_XID);
                String unbindXid = RootContext.unbind();
                if (log.isDebugEnabled()) {
                    log.debug("unbind[" + unbindXid + "] from RootContext");
                }

                if (!rpcXid.equalsIgnoreCase(unbindXid)) {
                    log.warn("xid in change during RPC from " + rpcXid + " to " + unbindXid);
                    // 调用过程有新的事务上下文开启，则不能清除
                    if (unbindXid != null) {
                        RootContext.bind(unbindXid);
                        log.warn("bind [" + unbindXid + "] back to RootContext");
                    }
                }
            }
        } catch (Throwable t) {

        }


    }
}
