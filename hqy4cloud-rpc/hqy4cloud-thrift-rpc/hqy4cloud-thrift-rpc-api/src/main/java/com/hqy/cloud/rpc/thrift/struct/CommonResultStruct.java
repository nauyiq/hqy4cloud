package com.hqy.cloud.rpc.thrift.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.cloud.common.result.Result;
import com.hqy.cloud.common.result.ResultCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14
 */
@Setter
@Getter
@ThriftStruct
public final class CommonResultStruct {

    @ThriftField(1)
    public boolean result;

    @ThriftField(2)
    public int code;

    @ThriftField(3)
    public String message;

    public CommonResultStruct() {
        this.result = true;
        this.code = ResultCode.SUCCESS.code;
        this.message = ResultCode.SUCCESS.message;
    }

    public CommonResultStruct(Result result) {
        this.result = false;
        this.code = result.getCode();
        this.message = result.getMessage();
    }

    public CommonResultStruct(boolean result, int code, String message) {
        this.result = result;
        this.code = code;
        this.message = message;
    }

    public static CommonResultStruct of() {
        return new CommonResultStruct();
    }

    public static CommonResultStruct of(Result resultCode) {
        return new CommonResultStruct(resultCode);
    }

}
