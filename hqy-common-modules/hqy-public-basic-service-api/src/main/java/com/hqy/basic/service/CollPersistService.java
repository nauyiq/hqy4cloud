package com.hqy.basic.service;

import com.hqy.basic.entity.ThrottledIpBlock;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 15:23
 */
public interface CollPersistService {

    void saveThrottledIpBlockHistory(ThrottledIpBlock throttledIpBlock);

}
