package com.hqy.cloud.registry.common.model;

import com.hqy.cloud.common.base.lang.StringConstants;

/**
 * ModelService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29
 */
public interface ModelService {

    /**
     * get application model.
     * @return {@link ApplicationModel}
     */
    ApplicationModel getModel();


    /**
     * model name.
     * @return default empty.
     */
    default String getModelName() {
        return StringConstants.EMPTY;
    }

}
