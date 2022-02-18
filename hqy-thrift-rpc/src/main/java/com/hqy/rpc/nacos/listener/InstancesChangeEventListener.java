package com.hqy.rpc.nacos.listener;

import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.nacos.NacosClient;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 节点实例改变事件监听器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/18 14:16
 */
@Slf4j
//@Component
@Deprecated
public class InstancesChangeEventListener extends Subscriber<InstancesChangeEvent> {

    public InstancesChangeEventListener() {
        log.info("@@@ register InstancesChangeEventListener.");
    }


    @PostConstruct
    public void post() {
        //注册当前监听器
        NotifyCenter.registerSubscriber(this);
    }

    @Override
    public void onEvent(InstancesChangeEvent instancesChangeEvent) {
        log.info("@@@ received InstancesChangeEvent, loadServerNode begin.");
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("@@@ instancesChangeEvent : {}", JsonUtil.toJson(instancesChangeEvent));
        }
        NacosClient client = SpringContextHolder.getBean(NacosClient.class);
        int count = client.loadServerNode();
        log.info("@@@ loadServerNode end, count :{}", count);
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }
}
