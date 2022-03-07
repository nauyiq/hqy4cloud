package com.hqy.coll.struct;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:57
 */
@ThriftStruct
public final class ThrottledIpBlockStruct {

    /**
     * 被什么方式节流的
     */
    @ThriftField(1)
    public String throttleBy;

    /**
     * 请求的客户端ip
     */
    @ThriftField(2)
    public String ip;

    /**
     * 请求url
     */
    @ThriftField(3)
    public String url;

    /**
     * request json
     */
    @ThriftField(4)
    public String accessJson ;

    /**
     * 封禁时间 单位s
     */
    @ThriftField(5)
    public Integer blockedSeconds;

    /**
     * 所属环境
     */
    @ThriftField(6)
    public String env;


    public ThrottledIpBlockStruct() {
    }

}
