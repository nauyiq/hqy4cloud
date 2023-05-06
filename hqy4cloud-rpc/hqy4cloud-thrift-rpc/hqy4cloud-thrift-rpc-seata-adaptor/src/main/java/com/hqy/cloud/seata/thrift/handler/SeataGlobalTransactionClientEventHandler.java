package com.hqy.cloud.seata.thrift.handler;

import com.facebook.ThriftRequestPram;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.core.RPCContext;
import com.hqy.cloud.rpc.thrift.service.ThriftContextClientHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftContext;
import io.seata.core.context.RootContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * seata transactional client handler.
 * seata transaction id transport.
 * @see RootContext
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 15:04
 */
public class SeataGlobalTransactionClientEventHandler implements ThriftContextClientHandleService {

    private static final Logger log = LoggerFactory.getLogger(SeataGlobalTransactionClientEventHandler.class);

    @Override
    public void doPreWrite(ThriftContext thriftContext, String methodName, Object[] args) {

        if (thriftContext.getRequestPram() == null) {
            log.warn("ThriftContext request parameter is null, current RPC context does not allow extended parameter passing.");
            return;
        }

        if (RPCContext.isGlobalTransactionalMethod(methodName)) {
            //need translate seata id
            String xid = RootContext.getXID();
            if (StringUtils.isBlank(xid)) {
                log.warn("Not found seata xid from RootContext.");
            }
            ThriftRequestPram requestPram = thriftContext.getRequestPram();
            requestPram.pram.put(CommonConstants.SEATA_XID, xid);
        } else {
            log.info("This method:{} not support global transactional.", methodName);
        }

    }
}
