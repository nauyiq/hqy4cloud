package com.hqy.cloud.netty.socketio.thrift;

import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;

import java.util.Map;
import java.util.Set;

/**
 * SocketIoThriftDiscovery.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/12
 */
public interface SocketIoThriftDiscovery {


    /**
     * 推送事件给指定的用户
     * @param applicationName 服务名
     * @param bizId           接收人bizId
     * @param eventName       事件名
     * @param eventJsonData   事件json data
     * @param serviceClass    service类型.
     * @param async           是否异步推送
     * @return                是否推送成功, 异步推送默认返回true
     */
    <T extends ThriftSocketIoPushService> boolean pushEvent(String applicationName, String bizId, String eventName,
                                                            String eventJsonData, Class<T> serviceClass, boolean async);


    /**
     * 不同于推送事件，当前方法无需判断用户与哪个服务建立了连接， 直接往所有的服务中推送事件
     * @param applicationName  服务名
     * @param bizId            接收人bizId
     * @param eventName        事件名
     * @param eventJsonData    事件json data
     * @param serviceClass     service类型.
     * @param async            是否异步推送
     * @return                 是否推送成功, 异步推送默认返回true
     */
    <T extends ThriftSocketIoPushService> boolean broadcastEvent(String applicationName, String bizId,  String eventName,
                                                                 String eventJsonData,  Class<T> serviceClass, boolean async);

    /**
     * 推送事件给批量的用户
     * @param applicationName 服务名
     * @param bizIds          推送给哪些人
     * @param eventName       事件名
     * @param eventJsonData   事件内容的json数据
     * @param serviceClass    推送的service类型
     * @param async           是否异步推送
     * @return                是否推送成功, 异步推送默认返回true
     */
    <T extends ThriftSocketIoPushService> boolean pushEvent(String applicationName, Set<String> bizIds, String eventName,
                                                             String eventJsonData, Class<T> serviceClass, boolean async);


    /**
     * 批量随送事件
     * @param applicationName 服务名
     * @param messages        key:接收人, value:接受数据
     * @param eventName       时间名
     * @param serviceClass    推送的service类型
     * @param async           是否异步推送
     * @return                是否推送成功, 异步推送默认返回true
     */
    <T extends ThriftSocketIoPushService> boolean pushEvent(String applicationName, Map<String, String> messages, String eventName, Class<T> serviceClass, boolean async);

    /**
     * 不同于推送事件，当前方法无需判断用户与哪个服务建立了连接， 直接往所有的服务中推送事件
     * @param applicationName  服务名
     * @param bizIds           推送给哪些人
     * @param eventName        事件名
     * @param eventJsonData    事件json data
     * @param serviceClass     service类型.
     * @param async            是否异步推送
     * @return                 是否推送成功, 异步推送默认返回true
     */
    <T extends ThriftSocketIoPushService> boolean broadcastEvent(String applicationName, Set<String> bizIds,  String eventName,
                                                                 String eventJsonData, Class<T> serviceClass, boolean async);


    /**
     * 推送事件给所有的客户端连接
     * @param applicationName 服务名
     * @param eventName       事件名
     * @param eventJsonData   事件内容json
     * @param serviceClass    service类型.
     * @param async           是否异步推送
     * @return                是否推送成功, 异步推送默认返回true
     */
    <T extends ThriftSocketIoPushService> boolean broadcastAll(String applicationName, String eventName, String eventJsonData, Class<T> serviceClass, boolean async);



    /**
     * 根据bizId获取thrift rpc service, 获取的是客户端所在rpc service的连接.
     * @param applicationName socket服务名
     * @param bizId           id
     * @param serviceClass    thrift rpc class type
     * @return                ThriftSocketIoPushService.
     */
    <T extends ThriftSocketIoPushService> T getSocketIoPushService(String applicationName, String bizId, Class<T> serviceClass);


    /**
     * 批量获取bizId所在的class map
     * @param applicationName socket服务名
     * @param bizIds          id set
     * @param serviceClass    thrift rpc class type
     * @return                ThriftSocketIoPushService of map
     */
    <T extends ThriftSocketIoPushService> Map<String, T> getMultipleSocketIoPushService(String applicationName, Set<String> bizIds, Class<T> serviceClass);

}
