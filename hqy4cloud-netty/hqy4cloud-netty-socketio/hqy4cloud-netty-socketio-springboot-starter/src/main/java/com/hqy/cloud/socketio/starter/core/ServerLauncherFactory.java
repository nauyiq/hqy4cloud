package com.hqy.cloud.socketio.starter.core;

/**
 * ServerLauncherFactory.
 * @see ServerLauncher
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 14:00
 */
public interface ServerLauncherFactory {

    /**
     * create ServerLauncher.
     * @return ServerLauncher.
     */
    ServerLauncher create();

}
