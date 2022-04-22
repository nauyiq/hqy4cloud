package com.hqy.rpc.thrift;

/**
 * thrift rpc上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/22 16:02
 */
public class ThriftContext {

    /**
     * 运行结果
     */
    private boolean result = true;

    /**
     * 通讯对手的 ip端口
     */
    private String communicationParty;

    /**
     * 开始时间
     */
    private long startTime = System.currentTimeMillis();

    /**
     * 读之前的时间
     */
    private long preReadTime;

    /**
     * 读之后的时间
     */
    private long postReadTime;

    /**
     * 写之前的时间
     */
    private long preWriteTime;

    /**
     * 写之后的时间
     */
    private long postWriteTime;

    /**
     * 是否采集
     */
    private boolean collection = true;

    /**
     * 请求的参数json
     */
    private String reqParamJson;

    /**
     * 远程调用过程中拓展参数
     */
    private RemoteExParam param;

    /**
     * 异常.
     */
    private Throwable throwable;


    public ThriftContext() {
    }


    public ThriftContext(String communicationParty) {
        this.communicationParty = communicationParty;
    }


    public String getRootId() {
        if(param == null){
            return "";
        }
        return param.rootId;
    }

    public String getParentId() {
        if(param == null){
            return "";
        }
        return param.parentId;
    }

    public String getChildId() {
        if(param == null){
            return "";
        }
        return param.childId;
    }



    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getCommunicationParty() {
        return communicationParty;
    }

    public void setCommunicationParty(String communicationParty) {
        this.communicationParty = communicationParty;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public String getReqParamJson() {
        return reqParamJson;
    }

    public void setReqParamJson(String reqParamJson) {
        this.reqParamJson = reqParamJson;
    }

    public RemoteExParam getParam() {
        return param;
    }

    public void setParam(RemoteExParam param) {
        this.param = param;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
