package com.hqy.collector.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.collector.dao.RPCMinuteFlowRecordDao;
import com.hqy.collector.entity.RPCFlowRecord;
import com.hqy.collector.service.RPCFlowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * RPCFlowRecordServiceImpl.
 * @author qiyuan.hong
 * @date 2022-03-17 21:27
 */
@Service
@RequiredArgsConstructor
public class RPCFlowRecordServiceImpl extends BaseTkServiceImpl<RPCFlowRecord, Long> implements RPCFlowRecordService {

    private final RPCMinuteFlowRecordDao dao;

    @Override
    public BaseDao<RPCFlowRecord, Long> getTkDao() {
        return dao;
    }
}
