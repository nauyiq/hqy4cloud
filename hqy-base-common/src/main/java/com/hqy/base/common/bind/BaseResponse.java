package com.hqy.base.common.bind;

import java.io.Serializable;

/**
 * @author qy
 * @date 2021-09-14 19:49
 */
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 802942119562864881L;

    /**
     * 业务是否执行成功.
     */
    private boolean result;

    public BaseResponse() {
        this(true);
    }

    public BaseResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
