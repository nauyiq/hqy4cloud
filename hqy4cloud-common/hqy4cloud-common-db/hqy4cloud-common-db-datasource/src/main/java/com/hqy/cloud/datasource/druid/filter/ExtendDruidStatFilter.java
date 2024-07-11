package com.hqy.cloud.datasource.druid.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.hqy.cloud.alarm.notification.common.NotificationType;
import com.hqy.cloud.alarm.notification.core.AlerterHolder;
import com.hqy.cloud.alarm.notification.core.email.EmailContent;
import com.hqy.cloud.coll.struct.SqlRecordStruct;
import com.hqy.cloud.collection.api.Collector;
import com.hqy.cloud.collection.common.BusinessCollectionType;
import com.hqy.cloud.collection.core.CollectorHolder;
import com.hqy.cloud.common.base.lang.DateMeasureConstants;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.datasource.core.CollectionModelUtil;
import com.hqy.cloud.datasource.core.SqlExceptionType;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 拓展 {@link com.alibaba.druid.filter.stat.StatFilter}
 * 新增处理慢sql/错sql事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8
 */
@Slf4j
public class ExtendDruidStatFilter extends StatFilter {
    private static final String SCHEDULE_NAME = "druid-states-reset";
    private static final AtomicLong COUNTER = new AtomicLong(1);
    private static final int FREQUENCY = 60;

    public ExtendDruidStatFilter(StatFilterConfig statFilterConfig) {
        super.setSlowSqlMillis(statFilterConfig.slowSqlMillis());
        super.setLogSlowSql(statFilterConfig.logSlowSql());
        super.setMergeSql(statFilterConfig.mergeSql());
        // 启动重置druid stat数据job 防止记录的sql数据太多导致oom.
        startResetStatDataJob();
    }

    private void startResetStatDataJob() {
        ScheduledExecutorService service = IExecutorsRepository.newSingleScheduledExecutor(SCHEDULE_NAME);
        service.scheduleAtFixedRate(this::doResetStatData, DateMeasureConstants.FIVE_MINUTES.toMillis(), DateMeasureConstants.ONE_MINUTES.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void doResetStatData() {
        long count = COUNTER.incrementAndGet();
        try {
            if (CommonSwitcher.ENABLE_SCHEDULE_RESET_DRUID_STATES.isOff()) {
                log.info("Switcher enable reset druid state is off.");
            } else {
                if (count % FREQUENCY == 0) {
                    // 重置所有数据.
                    DruidStatManagerFacade.getInstance().resetAll();
                }
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }
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
                AlerterHolder.getInstance().notify(NotificationType.EMAIL, emailContent);
            }

            if (MicroServiceConstants.COMMON_COLLECTOR.equals(ProjectContext.getContextInfo().getNameEn())) {
                // 采集服务的错误sql不进行采集
                return;
            }

            if (CommonSwitcher.ENABLE_DATABASE_ERROR_SQL_COLLECTION.isOn()) {
                // 获取采集器进行sql采集
                SqlRecordStruct struct = CollectionModelUtil.buildErrorSqlRecordStruct(params, sql, SystemClock.now(), costMills, error);
                Collector<SqlRecordStruct> collector = CollectorHolder.getInstance().getCollector(BusinessCollectionType.SQL);
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
                AlerterHolder.getInstance().notify(NotificationType.EMAIL, emailContent);
            }

            if (MicroServiceConstants.COMMON_COLLECTOR.equals(ProjectContext.getContextInfo().getNameEn())) {
                // 采集服务的慢sql不进行采集
                return;
            }

            if (CommonSwitcher.ENABLE_DATABASE_SLOW_SQL_COLLECTION.isOn()) {
                // 获取采集器进行sql采集
                SqlRecordStruct struct = CollectionModelUtil.buildSlowSqlRecordStruct(params, sql, SystemClock.now(), costMills);
                Collector<SqlRecordStruct> collector = CollectorHolder.getInstance().getCollector(BusinessCollectionType.SQL);
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
