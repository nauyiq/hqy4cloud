package com.hqy.mq.collector.service;


import com.hqy.mq.collector.entity.ThrottledIpBlock;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 15:23
 */
public interface CollPersistService {

    void saveThrottledIpBlockHistory(ThrottledIpBlock throttledIpBlock);

}
