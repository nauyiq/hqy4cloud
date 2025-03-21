package com.hqy.cloud.db.common.transaction;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hongqy
 * @date 2025/3/21
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TccRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * 事务ID
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


}
