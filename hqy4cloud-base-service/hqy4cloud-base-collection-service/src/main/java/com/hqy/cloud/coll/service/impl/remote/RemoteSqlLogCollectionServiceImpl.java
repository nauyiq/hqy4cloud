package com.hqy.cloud.coll.service.impl.remote;

import com.hqy.cloud.coll.converter.CollectorServiceConverter;
import com.hqy.cloud.coll.entity.SqlRecord;
import com.hqy.cloud.coll.service.RemoteSqlLogCollectionService;
import com.hqy.cloud.coll.service.SqlRecordTkService;
import com.hqy.cloud.coll.struct.SqlRecordStruct;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 9:44
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteSqlLogCollectionServiceImpl extends AbstractRPCService implements RemoteSqlLogCollectionService {
    private final SqlRecordTkService tkService;

    @Override
    public void addSqlRecord(SqlRecordStruct struct) {
        SqlRecord record = CollectorServiceConverter.CONVERTER.convert(struct);
        record.setDateTime();
        tkService.insert(record);
    }
}
