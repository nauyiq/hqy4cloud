package com.hqy.cloud.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.common.transaction.TccRequest;
import com.hqy.cloud.db.common.transaction.TransCancelSuccessType;
import com.hqy.cloud.db.common.transaction.TransactionActionStatus;
import lombok.*;

/**
 * 事务表,解决TCC的空回滚和悬挂问题等问题
 * <pre>
 *     1. 存储事务信息, 避免空回滚和悬挂问题.
 *     2. 需要业务库单独创建一个事务表
 *
 *     CREATE TABLE `transaction_log` (
 *    `id` bigint NOT NULL AUTO_INCREMENT,
 *    `created` datetime NOT NULL COMMENT '创建时间',
 *    `updated` datetime NOT NULL COMMENT '更新时间',
 *    `transaction_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事务id',
 *    `business_scene` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '业务场景',
 *    `business_module` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '业务模块',
 *    `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态',
 *    `version` int NULL COMMENT '版本号',
 *    `deleted` tinyint NULL COMMENT '逻辑删除字段',
 *    `cancel_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'cancel的类型',
 *    PRIMARY KEY (`id`),
 *    KEY `idx_businsess_trans_id`(`transaction_id`,`business_scene`,`business_module`) USING BTREE
 * ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb3 COLLATE=utf8_general_ci COMMENT='事务记录表';
 *
 * </pre>
 * @author hongqy
 * @date 2025/3/21
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_transaction_log")
public class TransactionLog extends CommonEntity {

    /**
     * 事务id
     */
    private String transactionId;

    /**
     * 业务场景
     */
    private String businessScene;

    /**
     * 业务模块
     */
    private String businessModule;

    /**
     * 状态
     */
    private TransactionActionStatus status;

    /**
     * cancel的类型
     */
    private TransCancelSuccessType cancelType;

    public static TransactionLog create(TccRequest request, TransactionActionStatus status) {
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setStatus(status);
        transactionLog.setTransactionId(request.getTransactionId());
        transactionLog.setBusinessScene(request.getBusinessScene());
        transactionLog.setBusinessModule(request.getBusinessModule());
        return transactionLog;
    }

    public static TransactionLog create(TccRequest request, TransactionActionStatus status, TransCancelSuccessType cancelType) {
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setCancelType(cancelType);
        transactionLog.setStatus(status);
        transactionLog.setTransactionId(request.getTransactionId());
        transactionLog.setBusinessScene(request.getBusinessScene());
        transactionLog.setBusinessModule(request.getBusinessModule());
        return transactionLog;
    }


}
