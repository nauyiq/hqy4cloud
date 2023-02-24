package com.hqy.rpc.thrift.support;

import com.hqy.rpc.core.ThriftRequestPram;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 15:52
 */
public abstract class Context {

    private final long startTime;

    private long preReadTime;

    private long postReadTime;

    private long preWriteTime;

    private long postWriteTime;

    private Throwable exception;

    private boolean result = true;

    private ThriftRequestPram requestPram;

    public Context() {
        this.startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getPreReadTime() {
        return preReadTime;
    }

    public void setPreReadTime(long preReadTime) {
        this.preReadTime = preReadTime;
    }

    public long getPostReadTime() {
        return postReadTime;
    }

    public void setPostReadTime(long postReadTime) {
        this.postReadTime = postReadTime;
    }

    public long getPreWriteTime() {
        return preWriteTime;
    }

    public void setPreWriteTime(long preWriteTime) {
        this.preWriteTime = preWriteTime;
    }

    public long getPostWriteTime() {
        return postWriteTime;
    }

    public void setPostWriteTime(long postWriteTime) {
        this.postWriteTime = postWriteTime;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ThriftRequestPram getRequestPram() {
        return requestPram;
    }

    public void setRequestPram(ThriftRequestPram requestPram) {
        this.requestPram = requestPram;
    }
}
