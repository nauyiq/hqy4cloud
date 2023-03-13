package com.hqy.rpc.thrift.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.rpc.api.service.RPCService;

import java.util.Set;

/**
 * ThriftSocketIoPushService.
 * Socket io server must extend this interface and using ${@link ThriftService}.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/22 17:47
 */
public interface ThriftSocketIoPushService extends RPCService {

    /**
     * 单发消息-同步
     * @param bizId         发给谁
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod
    boolean syncPush(@ThriftField(1) String bizId, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);


    /**
     * 群发消息-同步
     * @param bizIdSet      群发的用户集合
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod
    boolean syncPushMultiple(@ThriftField(1) Set<String> bizIdSet, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);


    /**
     * 单发消息-异步
     * @param bizId         发给谁
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod(oneway = true)
    void asyncPush(@ThriftField(1) String bizId, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);



    /**
     * 群发消息-异步
     * @param bizIdSet      群发的用户集合
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod(oneway = true)
    void asyncPushMultiple(@ThriftField(1) Set<String> bizIdSet, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);



}
