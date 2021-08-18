package com.hqy.rpc.nacos;

import org.springframework.context.ApplicationEvent;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-18 9:52
 */
public interface NodeHeartbeatListener {

    void onHeartbeat(ApplicationEvent heartbeatEvent);

}
