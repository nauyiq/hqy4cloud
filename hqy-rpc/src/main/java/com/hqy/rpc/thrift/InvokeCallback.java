package com.hqy.rpc.thrift;

/**
 * 配合RPC用的回调接口。 可选
 * @author qiyuan.hong
 * @date 2021-08-13 14:56
 */
public interface InvokeCallback {

	/**
	 * 执行rpc回调
	 * @param val obj对象
	 */
	void onInvokeResult(Object val);
	
}
