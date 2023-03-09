package com.hqy.cloud.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.rpc.thrift.struct.PageResultStruct;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 15:47
 */
@ThriftStruct
@NoArgsConstructor
public final class PageExceptionLogStruct extends PageResultStruct {

    @ThriftField(4)
    public List<PfExceptionStruct> resultList = new ArrayList<>();

    public PageExceptionLogStruct(int currentPage, long total, int pages, List<PfExceptionStruct> resultList) {
        super(currentPage, total, pages);
        this.resultList = resultList;
    }
}
