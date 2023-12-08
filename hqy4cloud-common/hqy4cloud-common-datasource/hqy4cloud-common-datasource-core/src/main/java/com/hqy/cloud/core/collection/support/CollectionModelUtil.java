package com.hqy.cloud.core.collection.support;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.foundation.collector.support.ExceptionCollectorUtils;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8 17:05
 */
@Slf4j
public class CollectionModelUtil {
    private static final int MAX_SELECT_SQL_LENGTH = 1000;

    /**
     * 构建慢sql CollectionModel对象
     * @param sql       sql
     * @param sqlTime   sql时间
     * @param costMills sql耗时
     * @return          {@link CollectionModel}
     */
    public static CollectionModel buildSlowModel(String sql, Long sqlTime, Long costMills) {
        if (StringUtils.isBlank(sql)) {
            log.warn("Slow sql can not empty.");
            return null;
        }
        try {
            sql = toSimpleSelectSql(sql);
            sqlTime = sqlTime == null ? System.currentTimeMillis() : sqlTime;
            ProjectContextInfo info = SpringContextHolder.getProjectContextInfo();
            return CollectionModel.builder()
                    .env(info.getEnv())
                    .sqlTime(sqlTime)
                    .sqlType(SqlType.SLOW)
                    .costMills(costMills)
                    .sql(sql)
                    .applicationInfo(info.getUip().toString()).build();
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return null;
        }
    }

    /**
     * 构建错误sql CollectionModel对象
     * @param sql     sql
     * @param sqlTime sql时间
     * @param reason  sql报错异常
     * @return        {@link CollectionModel}
     */
    public static CollectionModel buildErrorSql(String sql, Long sqlTime, Throwable reason) {
        if (StringUtils.isBlank(sql)) {
            log.warn("Error sql can not empty.");
            return null;
        }
        try {
            sqlTime = sqlTime == null ? System.currentTimeMillis() : sqlTime;
            ProjectContextInfo info = SpringContextHolder.getProjectContextInfo();
            return CollectionModel.builder()
                    .env(info.getEnv())
                    .sqlTime(sqlTime)
                    .reason(ExceptionCollectorUtils.getExceptionStackTrace(reason))
                    .sqlType(SqlType.ERROR)
                    .sql(sql)
                    .applicationInfo(info.getUip().toString()).build();
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return null;
        }
    }


    /**
     * 将select sql裁剪一下
     * @param sql 慢sql
     * @return    简化的sql
     */
    private static String toSimpleSelectSql(String sql) {
        sql = sql.trim();
        if (sql.trim().length() > MAX_SELECT_SQL_LENGTH) {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.debug("Sql is too long: " + "\r\n" + " {}.", sql);
            }
            int begin = sql.toUpperCase().indexOf(" FROM");
            return "SELECT * " + sql.substring(begin);
        }
        return sql;
    }




}
