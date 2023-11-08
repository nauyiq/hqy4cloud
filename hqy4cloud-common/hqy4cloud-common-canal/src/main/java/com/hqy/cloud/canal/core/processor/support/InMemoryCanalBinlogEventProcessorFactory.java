package com.hqy.cloud.canal.core.processor.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.core.processor.CanalBinlogEventProcessorFactory;
import com.hqy.cloud.canal.model.ModelTable;
import com.hqy.cloud.util.AssertUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:39
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PUBLIC, staticName = "of")
public class InMemoryCanalBinlogEventProcessorFactory implements CanalBinlogEventProcessorFactory {
    private final Map<ModelTable, List<BaseCanalBinlogEventProcessor<?>>> cache = MapUtil.newConcurrentHashMap(16);

    @Override
    public void register(ModelTable modelTable, BaseCanalBinlogEventProcessor<?> processor) {
        synchronized (cache) {
            cache.putIfAbsent(modelTable, new LinkedList<>());
            cache.get(modelTable).add(processor);
        }
    }

    @Override
    public List<BaseCanalBinlogEventProcessor<?>> get(ModelTable modelTable) {
        List<BaseCanalBinlogEventProcessor<?>> processors = cache.get(modelTable);
        AssertUtil.notEmpty(processors, String.format("Processor Not Found For %s", modelTable));
        return processors;
    }

}
