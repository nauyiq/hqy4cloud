package com.hqy.auth.access.server;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 网关 - 白名单管理中心
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 14:26
 */
public class GateWayWhiteListManager {

    private GateWayWhiteListManager() {}

    private static GateWayWhiteListManager instance = null;

    private static final Set<String> WHITE_LIST = new CopyOnWriteArraySet<>();

    public static GateWayWhiteListManager getInstance() {
        if (instance == null) {
            synchronized (GateWayWhiteListManager.class) {
                if (instance == null) {
                    instance = new GateWayWhiteListManager();
                }
            }
        }
        return instance;
    }

    static {
        WHITE_LIST.add("/oauth/**");
        WHITE_LIST.add("/auth/**");
        WHITE_LIST.add("/message/websocket/**");
    }

    public Set<String> whiteList() {
        return WHITE_LIST;
    }


    public void addWhite(String value) {
        WHITE_LIST.add(value);
    }






}
