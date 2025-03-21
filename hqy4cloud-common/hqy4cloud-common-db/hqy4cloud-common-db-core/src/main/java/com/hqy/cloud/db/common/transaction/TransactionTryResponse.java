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
public class TransactionTryResponse extends Response {

    public static TransactionTryResponse success(TransTrySuccessType transTrySuccessType) {
        TransactionTryResponse response = new TransactionTryResponse();
        response.setResult(true);
        response.setTransTrySuccessType(transTrySuccessType);
        return response;
    }

    public static TransactionTryResponse failed(String code, String message) {
        TransactionTryResponse response = new TransactionTryResponse();
        response.setResult(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }


    /**
     * 成功类型
     */
    private TransTrySuccessType transTrySuccessType;


}
