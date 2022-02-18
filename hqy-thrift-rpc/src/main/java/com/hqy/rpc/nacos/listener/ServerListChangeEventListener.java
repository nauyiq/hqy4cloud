package com.hqy.rpc.nacos.listener;

import com.alibaba.nacos.client.config.impl.ServerlistChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.hqy.rpc.nacos.NacosClient;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 服务列表变化监听器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/18 14:08
 */
@Slf4j
//@Component
@Deprecated
public class ServerListChangeEventListener extends Subscriber<ServerlistChangeEvent> {

    public ServerListChangeEventListener() {
        log.info("@@@ register ServerListChangeEventListener.");
    }

    @PostConstruct
    public void post() {
        //注册当前监听器
        NotifyCenter.registerSubscriber(this);
    }


    @Override
    public void onEvent(ServerlistChangeEvent serverlistChangeEvent) {
        log.info("@@@ received ServerlistChangeEvent start, loadServerNode begin.");
        NacosClient client = SpringContextHolder.getBean(NacosClient.class);
        int count = client.loadServerNode();
        log.info("@@@ loadServerNode end, count :{}", count);
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return ServerlistChangeEvent.class;
    }
}
