package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AccountBaseInfoStruct.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:11
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public final class AccountBaseInfoStruct {

    @ThriftField(1)
    public Long id;
    @ThriftField(2)
    public String nickname;
    @ThriftField(3)
    public String username;
    @ThriftField(4)
    public String email;
    @ThriftField(5)
    public String avatar;
    @ThriftField(6)
    public String roles;
    @ThriftField(7)
    public ChatgptConfigStruct chatgptConfig;

}
