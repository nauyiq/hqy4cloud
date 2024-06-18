package com.hqy.cloud.datasource.core;

import cn.hutool.core.date.SystemClock;
import com.hqy.cloud.coll.struct.SqlRecordStruct;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.foundation.event.collector.support.execption.ExceptionCollectorUtils;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.common.base.project.ProjectContextInfo;
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
     * @param params    请求参数
     * @param sql       sql
     * @param sqlTime   sql时间
     * @param costMills sql耗时
     * @return          {@link CollectionModel}
     */
    public static CollectionModel buildSlowModel(String params, String sql, Long sqlTime, Long costMills) {
        if (StringUtils.isBlank(sql)) {
            log.warn("Slow sql can not empty.");
            return null;
        }
        try {
            sql = toSimpleSelectSql(sql);
            sqlTime = sqlTime == null ? SystemClock.now() : sqlTime;
            ProjectContextInfo info = ProjectContext.getContextInfo();
            return CollectionModel.builder()
                    .env(info.getEnv())
                    .sqlTime(sqlTime)
                    .sqlExceptionType(SqlExceptionType.SLOW)
                    .costMills(costMills)
                    .sql(sql)
                    .applicationInfo(info.getUip().toString()).build();
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return null;
        }
    }

    /**
     * 构建慢sql SqlRecordStruct对象
     * @param params    请求参数
     * @param sql       sql
     * @param sqlTime   sql时间
     * @param costMills sql耗时
     * @return          {@link SqlRecordStruct}
     */
    public static SqlRecordStruct buildSlowSqlRecordStruct(String params, String sql, Long sqlTime, Long costMills) {
        if (StringUtils.isBlank(sql)) {
            log.warn("Slow sql can not empty.");
            return null;
        }
        try {
            sql = toSimpleSelectSql(sql);
            sqlTime = sqlTime == null ? SystemClock.now() : sqlTime;
            ProjectContextInfo info = ProjectContext.getContextInfo();
            return SqlRecordStruct.builder()
                    .env(info.getEnv())
                    .startTime(sqlTime)
                    .type(SqlExceptionType.SLOW.value)
                    .params(params)
                    .costMills(costMills)
                    .sql(sql)
                    .application(info.getUip().toString()).build();
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
            sqlTime = sqlTime == null ? SystemClock.now() : sqlTime;
            ProjectContextInfo info = ProjectContext.getContextInfo();
            return CollectionModel.builder()
                    .env(info.getEnv())
                    .sqlTime(sqlTime)
                    .reason(ExceptionCollectorUtils.getExceptionStackTrace(reason))
                    .sqlExceptionType(SqlExceptionType.ERROR)
                    .sql(sql)
                    .applicationInfo(info.getUip().toString()).build();
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return null;
        }
    }

    /**
     * 构建错误sql SqlRecordStruct对象
     * @param params     请求参数
     * @param sql        错误的sql
     * @param sqlTime    sql时间
     * @param costMills sql耗时
     * @param reason     异常的原因
     * @return           {@link SqlRecordStruct}
     */
    public static SqlRecordStruct buildErrorSqlRecordStruct(String params, String sql, Long sqlTime, Long costMills, Throwable reason) {
        if (StringUtils.isBlank(sql)) {
            log.warn("error sql can not empty.");
            return null;
        }
        try {
            sql = toSimpleSelectSql(sql);
            sqlTime = sqlTime == null ? SystemClock.now() : sqlTime;
            ProjectContextInfo info = ProjectContext.getContextInfo();
            return SqlRecordStruct.builder()
                    .env(info.getEnv())
                    .startTime(sqlTime)
                    .type(SqlExceptionType.ERROR.value)
                    .params(params)
                    .costMills(costMills)
                    .sql(sql)
                    .reason(ExceptionCollectorUtils.getExceptionStackTrace(reason))
                    .application(info.getUip().toString()).build();
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
