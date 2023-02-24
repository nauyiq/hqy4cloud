package com.hqy.admin.service.request;

import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 15:40
 */
public interface AdminErrorLogRequestService {

    /**
     * 分页查询错误日志
     * @param serviceName    服务名
     * @param type           错误类型
     * @param env            环境
     * @param exceptionClass 异常类
     * @param ip             ip
     * @param url            url
     * @param current        当前页
     * @param size           页行数
     * @return               DataResponse.
     */
    DataResponse pageErrorLog(String serviceName, String type, String env, String exceptionClass, String ip, String url, Integer current, Integer size);

    /**
     * 删除错误日志
     * @param id error log id
     * @return   MessageResponse.
     */
    MessageResponse deleteErrorLog(Long id);
}
