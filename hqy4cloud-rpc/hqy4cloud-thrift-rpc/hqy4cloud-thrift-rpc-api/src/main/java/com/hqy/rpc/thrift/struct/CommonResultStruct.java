package com.hqy.rpc.thrift.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.cloud.common.result.ResultCode;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 18:08
 */
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

    public CommonResultStruct(ResultCode resultCode) {
        this.result = false;
        this.code = resultCode.code;
        this.message = resultCode.message;
    }


    public CommonResultStruct(boolean result, int code, String message) {
        this.result = result;
        this.code = code;
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
