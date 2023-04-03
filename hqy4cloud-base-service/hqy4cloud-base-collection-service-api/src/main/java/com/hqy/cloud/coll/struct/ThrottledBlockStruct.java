package com.hqy.cloud.coll.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:57
 */
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class ThrottledBlockStruct {

    /**
     * id
     */
    @ThriftField(1)
    public Long id;

    /**
     * 被什么方式节流的
     */
    @ThriftField(2)
    public String throttleBy;

    /**
     * 请求的客户端ip
     */
    @ThriftField(3)
    public String ip;

    /**
     * 请求url
     */
    @ThriftField(4)
    public String url;

    /**
     * request json
     */
    @ThriftField(5)
    public String accessJson ;

    /**
     * 封禁时间 单位s
     */
    @ThriftField(6)
    public Integer blockedSeconds;

    /**
     * 所属环境
     */
    @ThriftField(7)
    public String env;

    /**
     * 创建时间
     */
    @ThriftField(8)
    public String created;


}
