package com.hqy.collector.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.collector.dao.RPCExceptionRecordDao;
import com.hqy.collector.entity.RPCExceptionRecord;
import com.hqy.collector.service.RPCExceptionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/5 15:24
 */
@Service
@RequiredArgsConstructor
public class RPCExceptionRecordServiceImpl extends BaseTkServiceImpl<RPCExceptionRecord, Long> implements RPCExceptionRecordService {

    private final RPCExceptionRecordDao dao;

    @Override
    public BaseDao<RPCExceptionRecord, Long> getTkDao() {
        return dao;
    }
}
