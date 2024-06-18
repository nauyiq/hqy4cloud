package com.hqy.cloud.collection.core.sql;

import com.hqy.cloud.coll.service.RemoteSqlLogCollectionService;
import com.hqy.cloud.coll.struct.SqlRecordStruct;
import com.hqy.cloud.collection.api.AbstractCollector;
import com.hqy.cloud.collection.common.BusinessCollectionType;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * sql采集器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12
 */
@Slf4j
public class SqlCollector extends AbstractCollector<SqlRecordStruct> {

    private final SqlCollectionConfigProperties properties;

    public SqlCollector(SqlCollectionConfigProperties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    public BusinessCollectionType type() {
        return BusinessCollectionType.SQL;
    }


    @Override
    protected void doCollect(SqlRecordStruct struct) {
        String sql = struct.sql;
        if (StringUtils.isNotBlank(sql) && sql.length() > properties.getMaxLength()) {
            struct.sql = sql.substring(0, properties.getMaxLength());
        }
        RemoteSqlLogCollectionService collectionService = RpcClient.getRemoteService(RemoteSqlLogCollectionService.class);
        collectionService.addSqlRecord(struct);
    }
}
