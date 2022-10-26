package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResourceStruct.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 16:34
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class ResourceStruct {

    /**
     * 资源id
     */
    @ThriftField(1)
    public Integer id;

    /**
     * 资源路径
     */
    @ThriftField(2)
    public String path;

    /**
     * 状态, 是否可用
     */
    @ThriftField(3)
    public Boolean status;
}
