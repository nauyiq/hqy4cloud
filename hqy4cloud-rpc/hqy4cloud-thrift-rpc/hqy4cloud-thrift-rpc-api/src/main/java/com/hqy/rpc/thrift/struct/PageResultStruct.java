package com.hqy.rpc.thrift.struct;

import com.facebook.swift.codec.ThriftField;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 10:59
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class PageResultStruct {

    /**
     * 当前页
     */
    @ThriftField(1)
    public int currentPage;

    /**
     * 总数
     */
    @ThriftField(2)
    public long total;

    /**
     * 总页数
     */
    @ThriftField(3)
    public int pages;






}
