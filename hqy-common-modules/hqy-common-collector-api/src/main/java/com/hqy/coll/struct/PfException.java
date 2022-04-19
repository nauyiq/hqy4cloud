package com.hqy.coll.struct;

import com.hqy.base.BaseEntity;

/**
 * 异常采集表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 14:08
 */
public class PfException extends BaseEntity<Long> {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 环境
     */
    private String environment;

    /**
     * 异常类
     */
    private String exceptionClass;

    /**
     * 异常堆栈
     */
    private String stackTrace;

    /**
     * 错误的业务状态码
     */
    private Integer resultCode;

    /**
     * url 针对web请求出错的才会采集
     */
    private String url;

    /**
     * ip 针对web请求出错的才会采集
     */
    private String ip;

    public PfException() {
        super();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
