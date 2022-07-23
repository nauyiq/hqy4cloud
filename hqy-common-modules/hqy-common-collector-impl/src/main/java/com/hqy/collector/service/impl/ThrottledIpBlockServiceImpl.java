package com.hqy.collector.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.collector.dao.ThrottledIpBlockDao;
import com.hqy.coll.entity.ThrottledIpBlock;
import com.hqy.collector.service.ThrottledIpBlockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-03-01 21:18
 */
@Service
public class ThrottledIpBlockServiceImpl extends BaseTkServiceImpl<ThrottledIpBlock, Long> implements ThrottledIpBlockService {

    @Resource
    private ThrottledIpBlockDao throttledIpBlockDao;

    @Override
    public BaseDao<ThrottledIpBlock, Long> selectDao() {
        return this.throttledIpBlockDao;
    }
}
