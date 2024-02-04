package com.hqy.cloud.db.service.impl;

import com.hqy.cloud.db.common.CreateTableSql;
import com.hqy.cloud.db.mapper.CommonMapper;
import com.hqy.cloud.db.service.CommonDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonDbServiceImpl implements CommonDbService {
    private final CommonMapper commonMapper;

    @Override
    public List<String> selectAllTableNameByDb(String db) {
        return commonMapper.getAllTableNameBySchema(db);
    }

    @Override
    public CreateTableSql selectTableCreateSql(String tableName) {
        return commonMapper.selectTableCreateSql(tableName);
    }

    @Override
    public void execute(String sql) {
        commonMapper.executeSql(sql);
    }
}
