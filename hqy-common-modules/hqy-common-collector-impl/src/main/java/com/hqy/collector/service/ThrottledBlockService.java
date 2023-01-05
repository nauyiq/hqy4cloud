package com.hqy.collector.service;

import com.facebook.swift.service.ThriftMethod;
import com.github.pagehelper.PageInfo;
import com.hqy.base.BaseTkService;
import com.hqy.coll.struct.PageThrottledBlockResultStruct;
import com.hqy.collector.entity.ThrottledBlock;
import com.hqy.rpc.thrift.struct.PageStruct;

/**
 * @author qiyuan.hong
 * @date 2022-03-01 21:16
 */
public interface ThrottledBlockService extends BaseTkService<ThrottledBlock, Long> {

    /**
     * 分页查询节流封禁数据
     * @param type       类型
     * @param throttleBy 模糊查询-节流方式
     * @param ip         模糊查询-ip
     * @param uri        模糊查询-uri
     * @param struct     分页参数
     * @return           PageInfo for ThrottledBlock.
     */
    @ThriftMethod
    PageInfo<ThrottledBlock> queryPage(Integer type, String throttleBy, String ip, String uri, PageStruct struct);
}
