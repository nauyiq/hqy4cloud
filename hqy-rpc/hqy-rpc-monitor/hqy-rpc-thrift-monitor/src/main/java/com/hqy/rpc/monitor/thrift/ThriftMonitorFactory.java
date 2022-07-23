package com.hqy.rpc.monitor.thrift;

import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.api.ProxyFactory;
import com.hqy.rpc.cluster.client.Client;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.monitor.Monitor;
import com.hqy.rpc.monitor.suport.AbstractMonitorFactory;
import com.hqy.rpc.monitor.thrift.api.ThriftMonitorService;
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

    private final ProxyFactory proxyFactory;

    private final Client client;

    public ThriftMonitorFactory(ProxyFactory proxyFactory, Client client) {
        this.proxyFactory = proxyFactory;
        this.client = client;
    }

    @Override
    protected Monitor createMonitor(RPCModel monitorRpcModel) {
        if (monitorRpcModel == null || StringUtils.isBlank(monitorRpcModel.getName())) {
            log.warn("Not create monitor, because not found monitor for application name.");
            return null;
        }
        Invoker<ThriftMonitorService> monitorServiceInvoker = client.getRemoteInvoker(ThriftMonitorService.class, monitorRpcModel.getName());
        ThriftMonitorService monitorService = proxyFactory.getProxy(monitorServiceInvoker, null);
        return new ThriftMonitor(monitorServiceInvoker, monitorService);
    }
}
