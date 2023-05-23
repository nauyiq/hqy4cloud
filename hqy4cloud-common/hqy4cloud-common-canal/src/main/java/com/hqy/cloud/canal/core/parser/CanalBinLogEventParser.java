package com.hqy.cloud.canal.core.parser;

import com.hqy.cloud.canal.model.CanalBinLogEvent;
import com.hqy.cloud.canal.model.CanalBinLogResult;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:49
 */
public interface CanalBinLogEventParser {

    /**
     * 解析binlog事件
     * @param event               事件
     * @param klass               目标类型
     * @param primaryKeyFunction  主键映射方法
     * @param commonEntryFunction 其他属性映射方法
     * @return CanalBinLogResult
     */
    <T> List<CanalBinLogResult<T>> parse(CanalBinLogEvent event,
                                         Class<T> klass,
                                         BasePrimaryKeyTupleFunction primaryKeyFunction,
                                         BaseCommonEntryFunction<T> commonEntryFunction);

}
