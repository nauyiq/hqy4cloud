package com.hqy.cloud.canal.core;

import com.hqy.cloud.canal.core.adapter.support.SourceAdapterFacade;
import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.core.processor.CanalBinlogEventProcessorFactory;
import com.hqy.cloud.canal.model.CanalBinLogEvent;
import com.hqy.cloud.canal.model.ModelTable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * DefaultCanalGlue.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 9:54
 */
public class DefaultCanalGlue implements CanalGlue {

    private final CanalBinlogEventProcessorFactory canalBinlogEventProcessorFactory;

    private DefaultCanalGlue(CanalBinlogEventProcessorFactory canalBinlogEventProcessorFactory) {
        this.canalBinlogEventProcessorFactory = canalBinlogEventProcessorFactory;
    }

    public static CanalGlue of(CanalBinlogEventProcessorFactory canalBinlogEventProcessorFactory) {
        return new DefaultCanalGlue(canalBinlogEventProcessorFactory);
    }

    @Override
    public void process(String content) {
        CanalBinLogEvent event = SourceAdapterFacade.X.adapt(CanalBinLogEvent.class, content);
        ModelTable modelTable = ModelTable.of(event.getDatabase(), event.getTable());
        List<BaseCanalBinlogEventProcessor<?>> baseCanalBinlogEventProcessors = this.canalBinlogEventProcessorFactory.get(modelTable);
        if (CollectionUtils.isEmpty(baseCanalBinlogEventProcessors)) {
            return;
        }
        baseCanalBinlogEventProcessors.forEach((processor) -> processor.process(event));
    }



}
