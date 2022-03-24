package com.hqy.base.common.bind;

import java.io.Serializable;


public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 802942119562864881L;
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
