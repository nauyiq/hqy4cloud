package com.hqy.cloud.netty.websocket.base.enums;

/**
 * 连接关闭的场景
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 16:34
 */
public enum CloseScene {

    /**
     * onclose 场景值 ：握手失败
     */
    SCENE_HANDSHAKE_FAIL,

    /**
     * onclose 场景值 ：客户端断开了链接
     */
    SCENE_CLIENT_END_DISCONNECT,

    /**
     * onclose 场景值 ：服务端断开连接
     */
    SCENE_SERVER_END_DISCONNECT

}
