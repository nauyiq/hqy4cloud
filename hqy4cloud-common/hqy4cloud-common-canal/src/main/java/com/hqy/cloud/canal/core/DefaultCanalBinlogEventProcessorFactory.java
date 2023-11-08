package com.hqy.cloud.canal.core;

import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.core.processor.CanalBinlogEventProcessorFactory;
import com.hqy.cloud.canal.model.ModelTable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * DefaultCanalBinlogEventProcessorFactory.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 9:55
 */
public class DefaultCanalBinlogEventProcessorFactory implements CanalBinlogEventProcessorFactory {
    private final ConcurrentMap<ModelTable, List<BaseCanalBinlogEventProcessor<?>>> cache = new ConcurrentHashMap<>(16);
    private DefaultCanalBinlogEventProcessorFactory() {}
    public static CanalBinlogEventProcessorFactory of() {
        return new DefaultCanalBinlogEventProcessorFactory();
    }

    @Override
    public void register(ModelTable modelTable, BaseCanalBinlogEventProcessor<?> processor) {
        synchronized(this.cache) {
            this.cache.putIfAbsent(modelTable, new LinkedList<>());
            this.cache.get(modelTable).add(processor);
        }
    }

    @Override
    public List<BaseCanalBinlogEventProcessor<?>> get(ModelTable modelTable) {
        return this.cache.get(modelTable);
    }
}
