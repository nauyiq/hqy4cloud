package com.hqy.cloud.db.common.transaction;

/**
 * @author hongqy
 * @date 2025/3/21
 */
public enum TransCancelSuccessType {

    /**
     * 回滚成功-TRY-CANCEL
     */
    CANCEL_AFTER_TRY_SUCCESS,

    /**
     * 回滚成功-TRY-CONFIRM-CANCEL
     */
    CANCEL_AFTER_CONFIRM_SUCCESS,

    /**
     * 空回滚
     */
    EMPTY_CANCEL,

    /**
     * 幂等成功
     */
    DUPLICATED_CANCEL;

}
