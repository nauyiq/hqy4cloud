package com.hqy.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.rpc.thrift.struct.PageResultStruct;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 11:02
 */
@ThriftStruct
@NoArgsConstructor
public final class PageThrottledBlockResultStruct extends PageResultStruct {

    @ThriftField(1)
    public List<ThrottledBlockStruct> throttledBlockList;


    public PageThrottledBlockResultStruct(int currentPage, long total, int pages, List<ThrottledBlockStruct> throttledBlockList) {
        super(currentPage, total, pages);
        this.throttledBlockList = throttledBlockList;
    }
}
