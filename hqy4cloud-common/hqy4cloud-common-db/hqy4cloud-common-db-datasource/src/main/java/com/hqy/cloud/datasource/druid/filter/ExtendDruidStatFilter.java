package com.hqy.cloud.datasource.druid.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.hqy.cloud.coll.struct.SqlRecordStruct;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.datasource.core.CollectionModelUtil;
import com.hqy.cloud.datasource.core.SqlExceptionType;
import com.hqy.cloud.foundation.event.alerter.AlerterHolder;
import com.hqy.cloud.foundation.event.collector.support.CollectorCenter;
import com.hqy.cloud.notice.email.EmailContent;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.event.collection.Collector;
import com.hqy.foundation.event.notice.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 拓展 {@link com.alibaba.druid.filter.stat.StatFilter}
 * 新增处理慢sql/错sql事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8 16:31
 */
@Slf4j
public class ExtendDruidStatFilter extends StatFilter {

    public ExtendDruidStatFilter(StatFilterConfig statFilterConfig) {
        super.setSlowSqlMillis(statFilterConfig.slowSqlMillis());
        super.setLogSlowSql(statFilterConfig.logSlowSql());
        super.setMergeSql(statFilterConfig.mergeSql());
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        // 发生异常之后, 处理错误的sql
        handlerErrorSql(statement, sql, error);
        super.statement_executeErrorAfter(statement, sql, error);
    }

    private void handlerErrorSql(StatementProxy statement, String sql, Throwable error) {
        if (ProjectContext.getContextInfo().isJustStarted(1)) {
            // 系统刚启动, 忽略异常sql采集
            return;
        }
        try {
            String params = this.buildSlowParameters(statement);
            long costMills = statement.getLastExecuteTimeNano() / NumberConstants.ONE_NANO_4MILLISECONDS;
            log.warn("Handler error sql. sql: {} | cost millis: {}.", sql, costMills);
            if (CommonSwitcher.ENABLE_EXCEPTION_SQL_ALTER.isOn()) {
                // 进行sql报警通知（发邮件）
                EmailContent emailContent = buildEmailNoticeContent(SqlExceptionType.ERROR, costMills, sql, params);
                AlerterHolder.getInstance().notify(EventType.SQL, NotificationType.EMAIL, emailContent);
            }

            if (MicroServiceConstants.COMMON_COLLECTOR.equals(ProjectContext.getContextInfo().getNameEn())) {
                // 采集服务的错误sql不进行采集
                return;
            }

            if (CommonSwitcher.ENABLE_DATABASE_ERROR_SQL_COLLECTION.isOn()) {
                // 获取采集器进行sql采集
                SqlRecordStruct struct = CollectionModelUtil.buildErrorSqlRecordStruct(params, sql, SystemClock.now(), costMills, error);
                Collector<SqlRecordStruct> collector = CollectorCenter.getInstance().getCollector(EventType.SQL);
                collector.collect(struct);
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }
    }


    @Override
    protected void handleSlowSql(StatementProxy statementProxy) {
        if (ProjectContext.getContextInfo().isJustStarted(NumberConstants.FIVE)) {
            // 系统刚启动, 忽略慢sql采集
            return;
        }
        try {
            // 是否开启慢SQL采集
            String sql = statementProxy.getLastExecuteSql();
            String params = this.buildSlowParameters(statementProxy);
            long costMills = statementProxy.getLastExecuteTimeNano() / NumberConstants.ONE_NANO_4MILLISECONDS;
            log.warn("Handler slow sql. sql: {} | cost millis: {}.", sql, costMills);
            if (CommonSwitcher.ENABLE_EXCEPTION_SQL_ALTER.isOn()) {
                // 进行sql报警通知（发邮件）
                EmailContent emailContent = buildEmailNoticeContent(SqlExceptionType.SLOW, costMills, sql, params);
                AlerterHolder.getInstance().notify(EventType.SQL, NotificationType.EMAIL, emailContent);
            }

            if (MicroServiceConstants.COMMON_COLLECTOR.equals(ProjectContext.getContextInfo().getNameEn())) {
                // 采集服务的慢sql不进行采集
                return;
            }

            if (CommonSwitcher.ENABLE_DATABASE_SLOW_SQL_COLLECTION.isOn()) {
                // 获取采集器进行sql采集
                SqlRecordStruct struct = CollectionModelUtil.buildSlowSqlRecordStruct(params, sql, SystemClock.now(), costMills);
                Collector<SqlRecordStruct> collector = CollectorCenter.getInstance().getCollector(EventType.SQL);
                collector.collect(struct);
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }
    }

    private EmailContent buildEmailNoticeContent(SqlExceptionType type, long costMillis, String sql, String params) {
        return EmailContent.builder()
                .subject(SqlExceptionType.class.getSimpleName() + StrUtil.COLON + type.name())
                .id(buildEventId(type, sql, params))
                .content("[SQL异常], 异常类型:" + type.name().concat(". costMills = " + costMillis).concat("\r\n")
                        .concat("sql: ").concat(sql).concat("\r\n")
                        .concat("params: ").concat(params))
                .build();
    }

    private String buildEventId(SqlExceptionType type, String sql, String params) {
        StringBuilder sb = new StringBuilder();
        sb.append(type.name())
                .append(StrUtil.UNDERLINE)
                .append(sql.length())
                .append(StrUtil.COLON)
                .append(sql.hashCode());
        if (StringUtils.isNotBlank(params)) {
            sb.append(params.length())
                    .append(StrUtil.COLON)
                    .append(params.hashCode());
        }
        return sb.toString();
    }


}
