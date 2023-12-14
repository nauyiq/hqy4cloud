package com.hqy.cloud.coll.service.impl;

import com.hqy.cloud.coll.entity.SqlRecord;
import com.hqy.cloud.coll.mapper.SqlRecordTkMapper;
import com.hqy.cloud.coll.service.SqlRecordTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 9:39
 */
@Service
@RequiredArgsConstructor
public class SqlRecordTkServiceImpl extends BaseTkServiceImpl<SqlRecord, Long> implements SqlRecordTkService {
    private final SqlRecordTkMapper mapper;

    @Override
    public BaseTkMapper<SqlRecord, Long> getTkMapper() {
        return mapper;
    }
}
