package com.hqy.cloud.db.common.transaction;

import com.hqy.cloud.common.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author hongqy
 * @date 2025/3/21
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCancelResponse extends Response {

    public static TransactionCancelResponse success(TransCancelSuccessType type) {
        TransactionCancelResponse response = new TransactionCancelResponse();
        response.setSuccess(true);
        response.setTransCancelSuccessType(type);
        return response;
    }

    public static TransactionCancelResponse failed(String code, String message) {
        TransactionCancelResponse response = new TransactionCancelResponse();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 成功类型
     */
    private TransCancelSuccessType transCancelSuccessType;
}
