package com.hqy.cloud.rpc.core;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/8 17:51
 */
public class InvokeResult {

    private boolean ok;
    private Object result;

    public InvokeResult() {
        this(false, null);
    }

    public InvokeResult(boolean ok, Object result) {
        this.ok = ok;
        this.result = result;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
