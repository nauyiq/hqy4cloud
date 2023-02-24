package com.hqy.rpc.monitor.thrift;

import com.hqy.rpc.api.ProxyFactory;
import com.hqy.rpc.cluster.client.Client;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.monitor.Monitor;
import com.hqy.rpc.monitor.suport.AbstractMonitorFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ThriftMonitorFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 11:27
 */
public class ThriftMonitorFactory extends AbstractMonitorFactory {
    private static final Logger log = LoggerFactory.getLogger(ThriftMonitorFactory.class);

    private final Client client;

    public ThriftMonitorFactory(Client client) {
        this.client = client;
    }

    @Override
    protected Monitor createMonitor(RPCModel monitorRpcModel) {
        if (monitorRpcModel == null || StringUtils.isBlank(monitorRpcModel.getName())) {
            log.warn("Not create monitor, because not found monitor for application name.");
            return null;
        }
        return new ThriftMonitor(client, monitorRpcModel);
    }
}
