package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.account.dto.AccountBaseInfoDTO;
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
public final class AccountBaseInfoStruct {

    @ThriftField(1)
    public Long id;
    @ThriftField(2)
    public String nickname;
    @ThriftField(3)
    public String username;
    @ThriftField(4)
    public String email;

    public AccountBaseInfoStruct(AccountBaseInfoDTO info) {
        this.id = info.getId();
        this.nickname = info.getNickname();
        this.username = info.getUsername();
        this.email = info.getEmail();
    }
}
