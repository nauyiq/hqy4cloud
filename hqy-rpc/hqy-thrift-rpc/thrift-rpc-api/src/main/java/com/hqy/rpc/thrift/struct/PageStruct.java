package com.hqy.rpc.thrift.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 11:06
 */
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class PageStruct {

    @ThriftField(1)
    public Integer pageNumber;

    @ThriftField(2)
    public Integer pageSize;


}
