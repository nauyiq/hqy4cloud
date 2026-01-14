package com.hqy.cloud.common.response;

import com.hqy.cloud.common.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Response implements Serializable, Result {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String code;
    private String message;


    public Response ofResult(boolean isSuccess, Result result) {
        this.success = isSuccess;
        this.code = result.getCode();
        this.message = result.getMessage();
        return this;
    }


}
