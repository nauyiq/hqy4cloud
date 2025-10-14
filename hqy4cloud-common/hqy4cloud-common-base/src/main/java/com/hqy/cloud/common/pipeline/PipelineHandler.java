package com.hqy.cloud.common.pipeline;

import com.hqy.cloud.common.base.exception.BizException;

/**
 * 管道处理器
 * @author hongqy
 * @date 2025/10/14
 */
public interface PipelineHandler<REQ> {

    /**
     * 执行核心逻辑
     * @param request
     */
    void handle(REQ request);

}
