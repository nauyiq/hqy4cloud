package com.hqy.cloud.rpc.thrift.support;

import com.facebook.ThriftRequestPram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private Map<String, Object> attachments = new ConcurrentHashMap<>();


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

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public Object getAttachment(String key) {
        return this.attachments.get(key);
    }

    public synchronized void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    public synchronized Map<String, Object> setAttachment(String key, Object value) {
        this.attachments.put(key, value);
        return this.attachments;
    }

}
