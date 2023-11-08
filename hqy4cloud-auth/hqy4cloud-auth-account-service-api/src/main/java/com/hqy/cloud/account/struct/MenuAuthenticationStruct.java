package com.hqy.cloud.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/19 10:25
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class MenuAuthenticationStruct implements Serializable {
    private static final long serialVersionUID = 301227626353284249L;

    @ThriftField(1)
    public String name;
    @ThriftField(2)
    public String path;
    @ThriftField(3)
    public String permission;



}
