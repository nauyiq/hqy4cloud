package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public RegistryAccountStruct(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
