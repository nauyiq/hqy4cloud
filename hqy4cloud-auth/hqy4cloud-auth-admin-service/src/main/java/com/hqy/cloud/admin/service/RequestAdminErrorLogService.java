package com.hqy.cloud.admin.service;

import com.hqy.cloud.common.bind.R;
import com.hqy.coll.struct.PageExceptionLogStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 15:40
 */
public interface RequestAdminErrorLogService {

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
     * @return               R.
     */
    R<PageExceptionLogStruct> pageErrorLog(String serviceName, String type, String env, String exceptionClass, String ip, String url, Integer current, Integer size);

    /**
     * 删除错误日志
     * @param id error log id
     * @return   R.
     */
    R<Boolean> deleteErrorLog(Long id);
}
