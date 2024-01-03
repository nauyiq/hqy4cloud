package com.hqy.cloud.registry.common.context;

import com.hqy.cloud.registry.common.model.ModelService;

/**
 * The lifecycle of service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 12:03
 */
public interface Lifecycle extends CloseableService, ModelService {

    /**
     * Initialize the component before {@link #start() start}
     */
    void initialize();

    /**
     * Start the component
     */
    void start();




}
