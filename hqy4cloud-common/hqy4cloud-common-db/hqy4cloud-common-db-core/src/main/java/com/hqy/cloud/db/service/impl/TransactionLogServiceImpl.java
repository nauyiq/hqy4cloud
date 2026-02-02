package com.hqy.cloud.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.db.common.transaction.*;
import com.hqy.cloud.db.entity.TransactionLog;
import com.hqy.cloud.db.mapper.TransactionLogMapper;
import com.hqy.cloud.db.service.TransactionLogService;

/**
 * @author hongqy
 * @date 2025/3/21
 */
public class TransactionLogServiceImpl extends ServiceImpl<TransactionLogMapper, TransactionLog> implements TransactionLogService {

    @Override
    public TransactionTryResponse tryTransaction(TccRequest tccRequest) {
        TransactionLog exist = getTransactionLog(tccRequest);
        if (exist == null) {
            // 新增TRY事务记录
            TransactionLog transactionLog = TransactionLog.create(tccRequest, TransactionActionStatus.TRY);
            return this.save(transactionLog) ? TransactionTryResponse.success(TransTrySuccessType.TRY_SUCCESS) : TransactionTryResponse.failed("TRY_FAILED", ResultCode.INSERT_FAILED.getMessage());
        }

        // 事务已经存在 幂等判断
        if (exist.getStatus() == TransactionActionStatus.TRY || exist.getStatus() == TransactionActionStatus.CANCEL || exist.getStatus() == TransactionActionStatus.CONFIRM) {
            TransactionTryResponse.success(TransTrySuccessType.DUPLICATED_TRY);
        }

        throw new UnsupportedOperationException("Unsupported status: " + exist.getStatus());
    }

    @Override
    public TransactionConfirmResponse confirmTransaction(TccRequest tccRequest) {
        TransactionLog exist = getTransactionLog(tccRequest);
        if (exist == null) {
            // 说明没有try过 直接抛异常
            throw new UnsupportedOperationException("Transaction can not confirm");
        }

        if (exist.getStatus() == TransactionActionStatus.TRY) {
            // 确认事务
            exist.setStatus(TransactionActionStatus.CONFIRM);
            return this.updateById(exist) ? TransactionConfirmResponse.success(TransConfirmSuccessType.CONFIRM_SUCCESS) : TransactionConfirmResponse.failed("CONFIRM_FAILED", ResultCode.UPDATE_FAILED.getMessage());
        }

        // 事务已经存在 幂等判断
        if (exist.getStatus() == TransactionActionStatus.CONFIRM) {
            return TransactionConfirmResponse.success(TransConfirmSuccessType.DUPLICATED_CONFIRM);
        }

        throw new UnsupportedOperationException("Transaction can not confirm, status: " + exist.getStatus());
    }

    @Override
    public TransactionCancelResponse cancelTransaction(TccRequest tccRequest) {
        TransactionLog exist = getTransactionLog(tccRequest);
        if (exist == null) {
            // 如果还没有Try，则直接记录一条状态为Cancel的数据，避免发生空回滚，并解决悬挂问题
            TransactionLog transactionLog = TransactionLog.create(tccRequest, TransactionActionStatus.CANCEL, TransCancelSuccessType.EMPTY_CANCEL);
            return this.save(transactionLog) ? TransactionCancelResponse.success(TransCancelSuccessType.EMPTY_CANCEL) : TransactionCancelResponse.failed("EMPTY_CANCEL_FAILED", ResultCode.INSERT_FAILED.getMessage());
        }

        if (exist.getStatus() == TransactionActionStatus.TRY || exist.getStatus() == TransactionActionStatus.CONFIRM) {
            // 状态是正常CANCEL, 即 TRY -> CANCEL OR CONFIRM -> CANCEL
            exist.setCancelType(exist.getStatus() == TransactionActionStatus.TRY ? TransCancelSuccessType.CANCEL_AFTER_TRY_SUCCESS : TransCancelSuccessType.CANCEL_AFTER_CONFIRM_SUCCESS);
            exist.setStatus(TransactionActionStatus.CANCEL);
            return this.updateById(exist) ? TransactionCancelResponse.success(exist.getCancelType()) : TransactionCancelResponse.failed("CANCEL_FAILED", ResultCode.UPDATE_FAILED.getMessage());
        }

        // 事务已经存在 幂等判断
        if (exist.getStatus() == TransactionActionStatus.CANCEL) {
            return TransactionCancelResponse.success(TransCancelSuccessType.DUPLICATED_CANCEL);
        }

        throw new UnsupportedOperationException("Transaction cancel failed, status: " + exist.getStatus());
    }

    private TransactionLog getTransactionLog(TccRequest request) {
        QueryWrapper<TransactionLog> query = Wrappers.query();
        query
                .eq("transaction_id", request.getTransactionId())
                .eq("business_scene", request.getBusinessScene())
                .eq("business_module", request.getBusinessModule());
        return this.getOne(query);
    }
}
