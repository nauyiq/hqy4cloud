package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/2 11:31
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class ChatgptConfigStruct {

    @ThriftField(1)
    public String apiKey;
    @ThriftField(2)
    public String accessToken;
    @ThriftField(3)
    public Integer maxTokens = 2048;
    @ThriftField(4)
    public Integer time = 10;
    @ThriftField(5)
    public String defaultModel;


}
