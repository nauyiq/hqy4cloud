package com.hqy.collector.service;

import com.github.pagehelper.PageInfo;
import com.hqy.base.BaseTkService;
import com.hqy.coll.struct.PageExceptionLogStruct;
import com.hqy.collector.entity.PfException;
import com.hqy.rpc.thrift.struct.PageStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 14:31
 */
public interface PfExceptionService extends BaseTkService<PfException, Long> {

    /**
     * 分页查询错误日志
     * @param serviceName    服务名
     * @param type           错误类型
     * @param env            环境
     * @param exceptionClass 异常类
     * @param ip             ip
     * @param url            url
     * @param struct         分页参数
     * @return               PageInfo
     */
    PageInfo<PfException> queryPage(String serviceName, String type, String env, String exceptionClass, String ip, String url, PageStruct struct);
}
