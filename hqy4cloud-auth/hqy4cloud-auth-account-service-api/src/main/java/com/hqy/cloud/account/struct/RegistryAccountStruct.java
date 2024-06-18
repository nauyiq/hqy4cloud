package com.hqy.cloud.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RegistryAccountStruct.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/17 11:02
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class RegistryAccountStruct {

    @ThriftField(1)
    public String username;
    @ThriftField(2)
    public String email;
    @ThriftField(3)
    public String password;
    @ThriftField(4)
    public String nickname;
    @ThriftField(5)
    public String avatar;
    @ThriftField(6)
    public String role;
    @ThriftField(7)
    public Long createBy;
    @ThriftField(8)
    public String phone;
    @ThriftField(9)
    public List<String> authorities;

    public RegistryAccountStruct(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }




}
