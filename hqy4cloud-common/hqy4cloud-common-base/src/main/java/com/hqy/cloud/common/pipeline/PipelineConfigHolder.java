package com.hqy.cloud.common.pipeline;

import org.springframework.core.NamedThreadLocal;

/**
 * @author hongqy
 * @date 2025/10/14
 */
public class PipelineConfigHolder {

    private final static ThreadLocal<Boolean> BREAK_FLAG = new NamedThreadLocal<>("PileLineBreakMap");

    public static void breakPipeline() {
        BREAK_FLAG.set(true);
    }

    public static Boolean getAndRemoveBreakFlag() {
        try {
            Boolean result = BREAK_FLAG.get();
            if (result != null) {
                return result;
            }
            return false;
        } finally {
            BREAK_FLAG.remove();
        }
    }




}
