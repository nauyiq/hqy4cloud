package com.hqy.cloud.rpc.monitor;

/**
 * MonitorService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/15
 */
public interface MonitorService {


    /**
     * Collect monitor data.
     * @param data collect data.
     */
    void collect(CollectionData data);


}
