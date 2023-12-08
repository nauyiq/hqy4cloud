package com.hqy.cloud.actuator.server;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;
import com.hqy.cloud.common.swticher.AbstractSwitcher;
import com.hqy.cloud.common.swticher.ServerSwitcher;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 升降级开关管理中心
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 9:45
 */
@Slf4j
public class GradeSwitcherCenter {
    private static volatile GradeSwitcherCenter instance;
    private volatile boolean initialize = false;
    private final Map<Integer, AbstractSwitcher> switchers = MapUtil.newConcurrentHashMap(64);

    public static GradeSwitcherCenter getInstance() {
        if (instance == null) {
            synchronized (GradeSwitcherCenter.class) {
                if (instance == null) {
                    instance = new GradeSwitcherCenter();
                }
            }
        }
        return instance;
    }

    private GradeSwitcherCenter() {
        initialize();
    }

    private void initialize() {
        if (initialize) {
            return;
        }
        try {
            // 获取当前节点上下文信息
            ProjectContextInfo info = SpringContextHolder.getProjectContextInfo();
            String applicationName = info.getNameEn();
            AbstractSwitcher[] allSwitchers = ServerSwitcher.allValues(ServerSwitcher.class);
            for (AbstractSwitcher switcher : allSwitchers) {
                if (switcher instanceof ServerSwitcher serverSwitcher && !serverSwitcher.getServerName().equals(applicationName)) {
                    // 不是当前服务的开关，则不注册
                    continue;
                }
                Integer switcherId = switcher.getId();
                if (switchers.containsKey(switcherId)) {
                    // 重复ID注册 业务上不允许这样设计
                    log.warn("Duplicate switcher id, please check all switcher ids.");
                    continue;
                }
                switchers.put(switcherId, switcher);
            }
        } finally {
            initialize = true;
        }

    }


    public void updateGradeSwitcherStatus(MicroServerSwitcherInfo info) {
        AbstractSwitcher switcher = switchers.get(info.getId());
        switcher.setStatus(info.getStatus());
    }

    public Map<Integer, AbstractSwitcher> getSwitchers() {
        return this.switchers;
    }



}
