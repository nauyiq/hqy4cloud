package com.hqy.cloud.datasource.druid.filter;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.filter.AutoLoad;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.hqy.cloud.util.config.SysProperty;
import com.hqy.cloud.util.sensitive.LogDesensitizedUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hongqy
 * @date 2026/1/28
 */
@Slf4j
@AutoLoad
@RequiredArgsConstructor
public class TraceSqlLogFilter extends FilterEventAdapter {

    private static final String PRINT_SQL = "sys.trace.sql-log.enabled";
    private static final String PRINT_SQL_DESENSITIZED  = "sys.trace.sql-log.desensitized";
    private static final String PRINT_FULL_SQL = "sys.trace.sql-log.completely";

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        super.statementExecuteAfter(statement, sql, result);
        if (SysProperty.get(PRINT_SQL, Boolean.class, true)) {
            if (SysProperty.get(PRINT_FULL_SQL, Boolean.class, false)) {
                sql = buildFullSql(statement, sql);
            }
            if (SysProperty.get(PRINT_SQL_DESENSITIZED, Boolean.class, false)) {
                String desensitized = LogDesensitizedUtil.desensitized(sql);
                log.info(desensitized);
            } else {
                log.info(sql);
            }
        }
    }

    private String buildFullSql(StatementProxy statement, String sql) {
        if (!(statement instanceof PreparedStatementProxy preparedStatement)) {
            return sql;
        }
        List<Object> params = new ArrayList<>();
        int parameterSize = preparedStatement.getParametersSize();
        for (int i = 0; i < parameterSize; i++) {
            JdbcParameter parameter = preparedStatement.getParameter(i);
            if (parameter != null) {
                params.add(parameter.getValue());
            } else {
                params.add(null);
            }
        }
        if (params.isEmpty()) {
            return sql;
        }
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        String fullSql = SQLUtils.format(sql, DbType.valueOf(dbType), params);
        fullSql = StrUtil.replace(fullSql, CharUtil.toString(CharUtil.LF), CharUtil.toString(CharUtil.SPACE));
        return fullSql.trim();
    }

}
