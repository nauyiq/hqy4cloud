package com.hqy.cloud.db.common.transaction;

/**
 * @author hongqy
 * @date 2025/3/21
 */
public enum TransTrySuccessType {

    /**
     * Try成功
     */
    TRY_SUCCESS,

    /**
     * 幂等成功
     */
    DUPLICATED_TRY;

}
