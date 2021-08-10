package com.hqy.basic.service;

import com.hqy.basic.dao.ThrottledIpBlockDao;
import com.hqy.basic.entity.ThrottledIpBlock;
import org.apache.dubbo.config.annotation.Service;

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
