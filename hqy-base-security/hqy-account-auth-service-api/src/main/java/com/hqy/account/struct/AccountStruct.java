package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.account.entity.Account;
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
    public String authorities;
    @ThriftField(6)
    public Boolean status;

    public AccountStruct(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.phone = account.getPhone();
        this.authorities = account.getAuthorities();
        this.status = account.getStatus();
    }
}
