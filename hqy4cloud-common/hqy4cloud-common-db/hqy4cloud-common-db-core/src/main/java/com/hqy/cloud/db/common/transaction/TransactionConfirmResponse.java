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
public class TransactionConfirmResponse extends Response {

    public static TransactionConfirmResponse success(TransConfirmSuccessType type) {
        TransactionConfirmResponse response = new TransactionConfirmResponse();
        response.setResult(true);
        response.setTransConfirmSuccessType(type);
        return response;
    }

    public static TransactionConfirmResponse failed(String code, String message) {
        TransactionConfirmResponse response = new TransactionConfirmResponse();
        response.setResult(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }


    /**
     * 成功类型
     */
    private TransConfirmSuccessType transConfirmSuccessType;

}
