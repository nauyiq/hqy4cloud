package com.hqy.cloud.thrift.client.support;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.core.RPCContext;
import com.hqy.cloud.rpc.monitor.CollectionData;
import com.hqy.cloud.rpc.monitor.Monitor;
import com.hqy.cloud.rpc.monitor.thrift.ThriftMonitor;
import com.hqy.cloud.rpc.thrift.service.ThriftContextClientHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hqy.cloud.rpc.monitor.Constants.*;

/**
 * do rpc collection.
 * @see ThriftMonitor
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/20 14:36
 */
public class CollectionClientEventHandler implements ThriftContextClientHandleService {
    private static final Logger log = LoggerFactory.getLogger(CollectionClientEventHandler.class);

    /**
     * do collection monitor.
     */
    private final Monitor monitor;

    public CollectionClientEventHandler(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void doDone(ThriftContext thriftContext, String methodName) {
        ThriftContextClientHandleService.super.doDone(thriftContext, methodName);
        if (monitor == null) {
            log.warn("Failed execute to do collection from client handler, monitor disabled.");
            return;
        }

        boolean needCollect = false;
        RPCContext rpcContext = thriftContext.getRpcContext();
        if (rpcContext != null) {
            needCollect = rpcContext.needCollect(methodName);
        }

        if (!needCollect) {
            if (log.isDebugEnabled()) {
                log.debug("Ignored collect this remoting, method:{}", methodName);
            }
            return;
        }

        CollectionData collectionData = new CollectionData(rpcContext.getCaller(), subStringProvider(rpcContext.getProvider()), rpcContext.getServiceClass(), rpcContext.getMethod(),
                thriftContext.getStartTime(), System.currentTimeMillis() - thriftContext.getStartTime(), thriftContext.isResult(), thriftContext.getException());

        //do collect.
        monitor.collect(collectionData);
    }


    private String subStringProvider(String provider) {
        if (StringUtils.isBlank(provider)) {
            return provider;
        }
        String prefix;
        switch (Environment.getInstance().getEnvironment()) {
            case Environment.ENV_TEST:
                prefix = TEST_PROVIDER_PREFIX;
                break;
            case Environment.ENV_PROD:
                prefix = PROD_PROVIDER_PREFIX;
                break;
            default:
                prefix = DEV_PROVIDER_PREFIX;
        }
        if (provider.startsWith(prefix)) {
            provider = provider.substring(provider.lastIndexOf(StringConstants.Symbol.AT) + 1);
        }
        return provider;

    }
}
