package com.hqy.rpc.cluster.router;

import lombok.Builder;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 16:57
 */
@Builder
public class RouterResult<T> {

    private final boolean needContinueRoute;
    private final List<T> result;
    private final String message;

    public RouterResult(List<T> result) {
        this.needContinueRoute = true;
        this.result = result;
        this.message = null;
    }

    public RouterResult(List<T> result, boolean needContinueRoute) {
        this.needContinueRoute = needContinueRoute;
        this.result = result;
        this.message = null;
    }

    public RouterResult(List<T> result, String message) {
        this.needContinueRoute = true;
        this.result = result;
        this.message = message;
    }

    public RouterResult(boolean needContinueRoute, List<T> result, String message) {
        this.needContinueRoute = needContinueRoute;
        this.result = result;
        this.message = message;
    }

    public boolean isNeedContinueRoute() {
        return needContinueRoute;
    }

    public List<T> getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }


}
