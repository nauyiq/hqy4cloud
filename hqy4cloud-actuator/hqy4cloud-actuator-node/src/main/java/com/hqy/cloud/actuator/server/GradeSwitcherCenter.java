package com.hqy.cloud.actuator.server;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;
import com.hqy.cloud.common.swticher.AbstractSwitcher;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.common.swticher.ServerSwitcher;
import com.hqy.cloud.rpc.core.Environment;
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
    private final Map<Integer, AbstractSwitcher> actuatorSwitchers = MapUtil.newConcurrentHashMap(64);
    private final Map<Integer, AbstractSwitcher> allSwitcherMap = MapUtil.newConcurrentHashMap(64);

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
                if (actuatorSwitchers.containsKey(switcherId)) {
                    // 重复ID注册 业务上不允许这样设计
                    log.warn("Duplicate switcher id, please check all switcher ids.");
                    continue;
                }
                allSwitcherMap.put(switcherId, switcher);
                if (!switcher.isRegisterActuator()) {
                    // 不注册到actuator
                    continue;
                }
                actuatorSwitchers.put(switcherId, switcher);
            }
        } finally {
            initialize = true;
        }

    }


    public void updateGradeSwitcherStatus(MicroServerSwitcherInfo info) {
        AbstractSwitcher switcher = actuatorSwitchers.get(info.getId());
        switcher.setStatus(info.getStatus());
    }

    public Map<Integer, AbstractSwitcher> getActuatorSwitchers() {
        return this.actuatorSwitchers;
    }

    public Map<Integer, AbstractSwitcher> getAllSwitchers() {
        return this.allSwitcherMap;
    }

    /**
     * 初始化某些开关
     */
    public void initializeSwitchers() {
        if (Environment.getInstance().isDevEnvironment()) {
            CommonSwitcher.ENABLE_THRIFT_RPC_COLLECT.setStatus(false);
            CommonSwitcher.ENABLE_DATABASE_SLOW_SQL_COLLECTION.setStatus(false);
            CommonSwitcher.ENABLE_DATABASE_ERROR_SQL_COLLECTION.setStatus(false);
            CommonSwitcher.ENABLE_EXCEPTION_COLLECTOR.setStatus(false);
            CommonSwitcher.ENABLE_EXCEPTION_SQL_ALTER.setStatus(false);
        } else {
            CommonSwitcher.JUST_4_TEST_DEBUG.setStatus(false);
        }

    }
}
