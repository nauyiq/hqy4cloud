package com.hqy.cloud.common.response;

import com.hqy.cloud.common.result.BsResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hongqy
 * @date 2025/1/24
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BsResponse implements Serializable, BsResult {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean result;
    private String code;
    private String message;

    public boolean isSuccess() {
        return this.result;
    }

}
