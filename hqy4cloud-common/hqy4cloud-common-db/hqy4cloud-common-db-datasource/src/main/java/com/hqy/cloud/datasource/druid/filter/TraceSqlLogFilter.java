package com.hqy.cloud.datasource.druid.filter;

import com.alibaba.druid.filter.AutoLoad;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.hqy.cloud.util.config.SysProperty;
import com.hqy.cloud.util.sensitive.LogDesensitizedUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hongqy
 * @date 2026/1/28
 */
@Slf4j
@AutoLoad
@RequiredArgsConstructor
public class TraceSqlLogFilter extends FilterEventAdapter {

    private static final String PRINT_SQL_KEY  = "sys.trace.sql.print";
    private static final String PRINT_SQL_DESENSITIZED  = "sys.trace.sql.desensitized";

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        super.statementExecuteAfter(statement, sql, result);
        if (SysProperty.get(PRINT_SQL_KEY, Boolean.class, true)) {
            if (SysProperty.get(PRINT_SQL_DESENSITIZED, Boolean.class, false)) {
                String desensitized = LogDesensitizedUtil.desensitized(sql);
                log.info(desensitized);
            } else {
                log.info(sql);
            }
        }
    }
}
