package com.hqy.cloud.registry.cluster.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.lock.service.LockService;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.cluster.MasterElectionService;
import com.hqy.cloud.registry.common.Constants;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 主节点选举service， 基于lock， 只要获取到锁就升级为主节点
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 * @see com.hqy.cloud.registry.cluster.ClusterService
 */
public record LockMasterServiceImpl(LockService lockService, Registry registry) implements MasterElectionService {
    private static final Logger log = LoggerFactory.getLogger(LockMasterServiceImpl.class);

    @Override
    public void elect(List<ServiceInstance> instances) {
        // 当前节点信息
        ServiceInstance instance = registry.getInstance();
        ProjectInfoModel model = instance.getApplicationModel();
        String host = instance.getHost();
        // 排除自己
        instances = instances.stream().filter(i -> !i.getHost().equals(host)).toList();
        // 是否是主节点标识
        boolean isMaster = instance.isMaster();
        // 是否存在其他主节点标识
        boolean alreadyMaster = instances.stream().anyMatch(ServiceInstance::isMaster);

        if (isMaster && !alreadyMaster) {
            // 自身已经是主节点，并且列表中不存在主节点了， 直接退出。
            return;
        }
        if (!isMaster && alreadyMaster) {
            // 如果自己不是主节点，并且存活列表中存在主节点，不在参与主节点竞争。
            return;
        }
        // 参与主节点竞争，通过获取锁方式竞争主节点
        String lockName = buildLockName(model);
        try {
            // 竞争主节点. 超时时间为1秒 1秒内获取不到锁则表示竞争主节点失败。
            boolean result = lockService.tryLock(lockName, Constants.DEFAULT_MASTER_INSTANCE_CHOOSE_LOCK_MILLIS, Constants.DEFAULT_MASTER_INSTANCE_CHOOSE_LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            if (result != isMaster) {
                // 更新自己
                model.getMetadataInfo().setMaster(result);
                registry.update(model);
            }
            log.info("Current instance elect master instance end, result: {}.", result);
        } catch (Throwable cause) {
            log.error("Failed execute to elect master instance, cause: {}.", cause.getMessage(), cause);
        } finally {
            lockService.unlock(lockName);
        }
    }

    public String buildLockName(ProjectInfoModel model) {
        String applicationName = model.getApplicationName();
        String group = model.getGroup();
        return applicationName + StrUtil.COLON + "clusterLock" + StrUtil.COLON + model.getGroup();
    }
}
