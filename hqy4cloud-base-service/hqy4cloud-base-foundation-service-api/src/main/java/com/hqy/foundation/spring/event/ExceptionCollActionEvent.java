package com.hqy.foundation.spring.event;

import com.hqy.cloud.common.result.ResultCode;
import com.hqy.foundation.common.enums.ExceptionType;
import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.Objects;

/**
 * 异常采集事件 继承spring的ApplicationEvent
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 11:41
 */
public class ExceptionCollActionEvent extends ApplicationEvent {

    private static final long serialVersionUID = -3800351556037011654L;

    public ExceptionCollActionEvent(Object source) {
        super(source);
    }

    public ExceptionCollActionEvent(ExceptionType type, Object source) {
        this(source);
        this.type = type;
    }

    public ExceptionCollActionEvent(ExceptionType type, Object source, Throwable exception, int step) {
        super(source);
        this.type = type;
        this.exception = exception;
        this.step = step;
    }


    /**
     * 异常类型
     */
    private ExceptionType type;

    /**
     * 发生的异常
     */
    private Throwable exception;

    /**
     * 是否需要过滤 防止采集过多
     */
    private boolean filter = true;

    /**
     * 抛出异常的时间
     */
    private final Date time = new Date();

    /**
     * url
     */
    private String url;

    /**
     * 客户端ip
     */
    private String ip;

    /**
     * 需要携带的异常的信息
     */
    private String param;

    /**
     * 需要采集的频次, 即发生了多少次此异常才进行采集 默认100
     */
    private int step = 100;

    /**
     * 异常的状态码
     */
    private ResultCode resultCode = ResultCode.SYSTEM_ERROR;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExceptionCollActionEvent that = (ExceptionCollActionEvent) o;
        return filter == that.filter && step == that.step && Objects.equals(exception, that.exception) && Objects.equals(time, that.time) && Objects.equals(param, that.param) && resultCode == that.resultCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resultCode == null) ? 0 : resultCode.hashCode());
        result = prime * result + ((exception == null) ? 0 : exception.getClass().getName().hashCode() +
                (exception.getMessage() == null? 0: exception.getMessage().hashCode()));
        result = prime * result + step;
        result = prime * result + ((super.getSource() == null) ? 0 : super.getSource().toString().hashCode());
        return result;
    }

    public ExceptionType getType() {
        return type;
    }

    public void setType(ExceptionType type) {
        this.type = type;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public Date getTime() {
        return time;
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

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
