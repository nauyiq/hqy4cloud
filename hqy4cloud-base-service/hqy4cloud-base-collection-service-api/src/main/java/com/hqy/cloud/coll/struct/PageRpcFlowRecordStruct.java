package com.hqy.cloud.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.rpc.thrift.struct.PageResultStruct;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:30
 */
@ThriftStruct
@NoArgsConstructor
public final class PageRpcFlowRecordStruct extends PageResultStruct {

    @ThriftField(4)
    public List<RpcFlowRecordStruct> resultList = Collections.emptyList();

    public PageRpcFlowRecordStruct(int currentPage, long total, int pages, List<RpcFlowRecordStruct> resultList) {
        super(currentPage, total, pages);
        this.resultList = resultList;
    }

}
