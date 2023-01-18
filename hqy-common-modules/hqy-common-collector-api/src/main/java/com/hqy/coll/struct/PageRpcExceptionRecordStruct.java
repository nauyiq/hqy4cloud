package com.hqy.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.rpc.thrift.struct.PageResultStruct;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:37
 */
@ThriftStruct
@NoArgsConstructor
public final class PageRpcExceptionRecordStruct extends PageResultStruct {

    @ThriftField(4)
    public List<RpcExceptionRecordStruct> resultList;

    public PageRpcExceptionRecordStruct(int currentPage, long total, int pages, List<RpcExceptionRecordStruct> resultList) {
        super(currentPage, total, pages);
        this.resultList = resultList;
    }


}
