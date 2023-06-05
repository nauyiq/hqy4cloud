package com.hqy.cloud.rpc.deploy;

import com.hqy.cloud.rpc.model.ApplicationModel;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 17:32
 */
public interface ApplicationDeployer extends Deployer<ApplicationModel>{

    /**
     * 获取application model.
     * @return ApplicationModel。
     */
    ApplicationModel getModel();

    /**
     * get ExecutorRepository
     * @return {@link ExecutorRepository}
     */
    ExecutorRepository getExecutorRepository();

    /**
     * Pre-processing before destroy model
     */
    void preDestroy();

    /**
     * Post-processing after destroy model
     */
    void postDestroy();



}
