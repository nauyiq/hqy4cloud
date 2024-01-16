package com.hqy.cloud.foundation.event.collector.support.sql;

import com.hqy.cloud.coll.service.RemoteSqlLogCollectionService;
import com.hqy.cloud.coll.struct.SqlRecordStruct;
import com.hqy.cloud.foundation.event.collector.AbstractCollector;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.foundation.common.EventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * sql采集器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 17:31
 */
@Slf4j
public class SqlCollector extends AbstractCollector<SqlRecordStruct> {

    /**
     * sql最大长度.
     */
    @Value("${collector.sql.max-length:4096}")
    private int maxLength;

    @Override
    public EventType type() {
        return EventType.SQL;
    }



    @Override
    protected void doCollect(SqlRecordStruct struct) {
        String sql = struct.sql;
        if (StringUtils.isNotBlank(sql) && sql.length() > maxLength) {
            struct.sql = sql.substring(0, maxLength);
        }
        RemoteSqlLogCollectionService collectionService = RpcClient.getRemoteService(RemoteSqlLogCollectionService.class);
        collectionService.addSqlRecord(struct);
    }
}
