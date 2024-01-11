package com.hqy.cloud.rpc.monitor;

import com.hqy.cloud.rpc.model.RpcModel;

/**
 * MonitorFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 9:40
 */
public interface MonitorFactory {

    /**
     * create monitor.
     * @param rpcModel {@link RpcModel}
     * @return           Monitor.
     */
    Monitor getMonitor(RpcModel rpcModel);

}
