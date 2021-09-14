package com.hqy.mq.collector.service;


import com.hqy.mq.collector.dao.ThrottledIpBlockDao;
import com.hqy.mq.collector.entity.ThrottledIpBlock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 15:32
 */
@Service
public class CollPersistServiceImpl implements CollPersistService {

    @Resource
    private ThrottledIpBlockDao throttledIpBlockDao;

    @Override
    public void saveThrottledIpBlockHistory(ThrottledIpBlock throttledIpBlock) {
        throttledIpBlockDao.insert(throttledIpBlock);
    }
}
