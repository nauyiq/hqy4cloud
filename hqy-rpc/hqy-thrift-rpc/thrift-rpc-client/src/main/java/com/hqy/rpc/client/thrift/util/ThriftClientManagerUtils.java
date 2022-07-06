package com.hqy.rpc.client.thrift.util;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NiftyClientChannel;
import com.hqy.util.AssertUtil;

import static com.hqy.rpc.client.thrift.ThriftClientManagerWrapper.CLIENT;

/**
 * @author qiyuan.hong
 * @date 2022-07-06 23:37
 */
public class ThriftClientManagerUtils {

    public static <T> T createClient(FramedClientConnector connector, Class<T> serviceClass) throws Exception {
        AssertUtil.notNull(connector, "FramedClientConnector should not be null.");
        AssertUtil.notNull(serviceClass, "Service class should not be null.");
        return CLIENT.getClientManager().createClient(connector, serviceClass).get();
    }

    public static <T> NiftyClientChannel getClientChannel(T t) {
        return (NiftyClientChannel) CLIENT.getClientManager().getRequestChannel(t);
    }

}
