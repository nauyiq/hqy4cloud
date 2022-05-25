package com.hqy.storage.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.common.entity.storage.SecKillOrder;
import com.hqy.storage.dao.SecKillOrderDao;
import com.hqy.storage.service.SecKillOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-05-25 23:43
 */
@Service
public class SecKillOrderServiceImpl extends BaseTkServiceImpl<SecKillOrder, Long> implements SecKillOrderService {

    @Resource
    private SecKillOrderDao dao;

    @Override
    public BaseDao<SecKillOrder, Long> selectDao() {
        return dao;
    }
}
