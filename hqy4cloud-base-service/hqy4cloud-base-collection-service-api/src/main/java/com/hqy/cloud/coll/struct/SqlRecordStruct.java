package com.hqy.cloud.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 9:30
 */
@Data
@Builder
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class SqlRecordStruct {

    @ThriftField(1)
    public Long id;

    @ThriftField(2)
    public String application;

    @ThriftField(3)
    public Integer type;

    @ThriftField(4)
    public Long startTime;

    @ThriftField(5)
    public Long costMills;

    @ThriftField(6)
    public String params;

    @ThriftField(7)
    public String reason;

    @ThriftField(8)
    public String env;

    @ThriftField(9)
    public String sql;
}
