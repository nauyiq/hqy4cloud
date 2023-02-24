package com.hqy.rpc.monitor.thrift.api;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/19 13:56
 */
public class StatisticsFlowItem {

    private int success;

    private int failure;

    private int total;

    private long interval;

    private Map<String, Integer> methodDetail;

    private Map<String, Integer> serviceDetail;

    public StatisticsFlowItem() {
    }

    public StatisticsFlowItem(int success, int failure, int total, long interval) {
        this.success = success;
        this.failure = failure;
        this.total = total;
        this.interval = interval;
    }

    public StatisticsFlowItem(int success, int failure, int total, long interval, Map<String, Integer> methodDetail, Map<String, Integer> serviceDetail) {
        this.success = success;
        this.failure = failure;
        this.total = total;
        this.interval = interval;
        this.methodDetail = methodDetail;
        this.serviceDetail = serviceDetail;
    }

    public void setItem(int success, int failure, int total, long interval, Map<String, Integer> methodDetail, Map<String, Integer> serviceDetail) {
        this.success = success;
        this.failure = failure;
        this.total = total;
        this.interval = interval;
        this.methodDetail = methodDetail;
        this.serviceDetail = serviceDetail;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public Map<String, Integer> getMethodDetail() {
        return methodDetail;
    }

    public void setMethodDetail(Map<String, Integer> methodDetail) {
        this.methodDetail = methodDetail;
    }

    public Map<String, Integer> getServiceDetail() {
        return serviceDetail;
    }

    public void setServiceDetail(Map<String, Integer> serviceDetail) {
        this.serviceDetail = serviceDetail;
    }
}
