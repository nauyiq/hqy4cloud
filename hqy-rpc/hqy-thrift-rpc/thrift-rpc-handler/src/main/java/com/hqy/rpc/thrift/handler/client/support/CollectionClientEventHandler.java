package com.hqy.rpc.thrift.handler.client.support;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.rpc.common.config.EnvironmentConfig;
import com.hqy.rpc.common.support.RPCContext;
import com.hqy.rpc.monitor.CollectionData;
import com.hqy.rpc.monitor.Monitor;
import com.hqy.rpc.thrift.support.ThriftContext;
import com.hqy.rpc.thrift.service.ThriftContextClientHandleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hqy.rpc.monitor.Constants.*;

/**
 * do rpc collection.
 * @see com.hqy.rpc.monitor.thrift.ThriftMonitor
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
        switch (EnvironmentConfig.getInstance().getEnvironment()) {
            case EnvironmentConfig.ENV_TEST:
                prefix = TEST_PROVIDER_PREFIX;
                break;
            case EnvironmentConfig.ENV_PROD:
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
