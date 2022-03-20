package com.hqy.coll.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.coll.dao.RPCMinuteFlowRecordDao;
import com.hqy.coll.entity.RPCMinuteFlowRecord;
import com.hqy.coll.service.RPCMinuteFlowRecordService;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @date 2022-03-17 21:27
 */
@Service
public class RPCMinuteFlowRecordServiceImpl extends BaseTkServiceImpl<RPCMinuteFlowRecord, Long> implements RPCMinuteFlowRecordService {


    @Override
    public BaseDao<RPCMinuteFlowRecord, Long> selectDao() {
        return SpringContextHolder.getBean(RPCMinuteFlowRecordDao.class);
    }
}
