package com.hqy.rpc.thrift;

/**
 * 配合RPC用的回调接口。 可选
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 14:56
 */
public interface InvokeCallback {

	void onInvokeResult(Object val);
	
}
