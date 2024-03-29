package com.hqy.cloud.coll.service;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.coll.entity.ThrottledBlock;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;

/**
 * @author qiyuan.hong
 * @date 2022-03-01 21:16
 */
public interface ThrottledBlockService extends BaseTkService<ThrottledBlock, Long> {

    /**
     * 分页查询节流封禁数据
     * @param throttleBy 模糊查询-节流方式
     * @param ip         模糊查询-ip
     * @param uri        模糊查询-uri
     * @param struct     分页参数
     * @return           PageInfo for ThrottledBlock.
     */
    PageInfo<ThrottledBlock> queryPage(String throttleBy, String ip, String uri, PageStruct struct);
}
