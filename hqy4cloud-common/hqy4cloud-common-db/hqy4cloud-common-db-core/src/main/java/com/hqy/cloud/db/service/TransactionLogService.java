package com.hqy.cloud.db.service;

import com.hqy.cloud.db.common.transaction.TccRequest;
import com.hqy.cloud.db.common.transaction.TransactionCancelResponse;
import com.hqy.cloud.db.common.transaction.TransactionConfirmResponse;
import com.hqy.cloud.db.common.transaction.TransactionTryResponse;

/**
 * @author hongqy
 * @date 2025/3/21
 */
public interface TransactionLogService {

    /**
     * TCC事务的TRY
     * @param tccRequest 入参
     * @return           响应结果
     */
    TransactionTryResponse tryTransaction(TccRequest tccRequest);

    /**
     * TCC事务的CONFIRM
     * @param tccRequest 入参
     * @return           响应结果
     */
    TransactionConfirmResponse confirmTransaction(TccRequest tccRequest);

    /**
     * TCC事务的CANCEL
     * @param tccRequest 入参
     * @return           响应结果
     */
    TransactionCancelResponse cancelTransaction(TccRequest tccRequest);
}
