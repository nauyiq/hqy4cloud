package com.hqy.cloud.druid;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

/**
 * 拓展 {@link com.alibaba.druid.filter.stat.StatFilter}
 * 新增处理慢sql/错sql事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8 16:31
 */
@RequiredArgsConstructor
public class ExtendDruidStatFilter extends StatFilter implements InitializingBean {
    private final DruidProperties druidProperties;

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        super.statement_executeErrorAfter(statement, sql, error);
    }

    @Override
    protected void handleSlowSql(StatementProxy statementProxy) {
        if (ProjectContextInfo.isJustStarted(NumberConstants.FIVE)) {
            // 系统刚启动, 忽略慢sql采集
            return;
        }
        if (CommonSwitcher.ENABLE_DATABASE_SLOW_SQL_COLLECTION.isOn()) {
            try {
                // 是否开启慢SQL采集
                String sql = statementProxy.getLastExecuteSql();
                String params = this.buildSlowParameters(statementProxy);
                long costMills = statementProxy.getLastExecuteTimeNano() / NumberConstants.ONE_NANO_4MILLISECONDS;

            } catch (Throwable cause) {

            }
        }





        super.handleSlowSql(statementProxy);
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        super.setSlowSqlMillis(druidProperties.getSlowSqlMillis());
        super.setLogSlowSql(true);
    }
}
