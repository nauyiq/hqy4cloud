package com.hqy.cloud.db.common.transaction;

/**
 * @author hongqy
 * @date 2025/3/21
 */
public enum TransConfirmSuccessType {

    /**
     * Confirm成功
     */
    CONFIRM_SUCCESS,

    /**
     * 幂等成功
     */
    DUPLICATED_CONFIRM;
}
