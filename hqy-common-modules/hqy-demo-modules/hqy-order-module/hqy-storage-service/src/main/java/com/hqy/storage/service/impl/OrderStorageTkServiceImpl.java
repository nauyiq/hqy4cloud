package com.hqy.storage.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.common.entity.OrderStorage;
import com.hqy.storage.dao.OrderStorageDao;
import com.hqy.storage.service.OrderStorageTkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/23 16:36
 */
@Service
public class OrderStorageTkServiceImpl extends BaseTkServiceImpl<OrderStorage, Long> implements OrderStorageTkService {

    @Resource
    private OrderStorageDao dao;

    @Override
    public BaseDao<OrderStorage, Long> selectDao() {
        return dao;
    }
}
