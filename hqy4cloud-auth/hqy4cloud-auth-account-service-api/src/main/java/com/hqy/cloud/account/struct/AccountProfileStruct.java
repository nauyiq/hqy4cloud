package com.hqy.cloud.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 18:08
 */
@Data
@Builder
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class AccountProfileStruct {

    @ThriftField(1)
    public Long id;
    @ThriftField(2)
    public String username;
    @ThriftField(3)
    public String nickname;
    @ThriftField(4)
    public String avatar;
    @ThriftField(5)
    public String intro;
    @ThriftField(6)
    public String birthday;
    @ThriftField(7)
    public Integer sex;

}
