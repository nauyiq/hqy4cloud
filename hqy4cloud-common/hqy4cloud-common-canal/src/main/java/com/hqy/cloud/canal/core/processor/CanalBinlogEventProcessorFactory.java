package com.hqy.cloud.canal.core.processor;

import com.hqy.cloud.canal.model.ModelTable;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:36
 */
public interface CanalBinlogEventProcessorFactory {

    /**
     * 通过数据库model获取binlog执行器
     * @param modelTable {@link ModelTable}
     * @return           {@link BaseCanalBinlogEventProcessor}
     */
    List<BaseCanalBinlogEventProcessor<?>> get(ModelTable modelTable);
    void register(ModelTable modelTable, BaseCanalBinlogEventProcessor<?> processor);




}
