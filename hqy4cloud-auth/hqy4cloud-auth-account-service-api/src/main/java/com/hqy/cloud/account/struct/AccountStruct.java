package com.hqy.cloud.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AccountStruct.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/19 13:29
 */
@Data
@ThriftStruct
@AllArgsConstructor
@NoArgsConstructor
public final class AccountStruct {

    @ThriftField(1)
    public Long id;
    @ThriftField(2)
    public String username;
    @ThriftField(3)
    public String email;
    @ThriftField(4)
    public String phone;
    @ThriftField(5)
    public String roles;
    @ThriftField(6)
    public Boolean status;
    @ThriftField(7)
    public Long created;

}
