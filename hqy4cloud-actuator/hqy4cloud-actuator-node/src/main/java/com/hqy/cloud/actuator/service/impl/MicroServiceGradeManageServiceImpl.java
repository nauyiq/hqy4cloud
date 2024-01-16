package com.hqy.cloud.actuator.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.actuator.core.GradeSwitcherListener;
import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;
import com.hqy.cloud.actuator.server.GradeSwitcherCenter;
import com.hqy.cloud.actuator.server.GradeSwitcherListenerRepository;
import com.hqy.cloud.actuator.service.MicroServiceGradeManageService;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.AbstractSwitcher;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.PubMode;
import com.hqy.cloud.rpc.CommonConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.hqy.cloud.common.base.lang.StringConstants.CONSUMER;
import static com.hqy.cloud.common.base.lang.StringConstants.PROVIDER;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 13:33
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MicroServiceGradeManageServiceImpl implements MicroServiceGradeManageService {
    private final Registry registry;

    @Override
    public Map<String, Object> getServerGradeInfo() {
        ServiceInstance instance = registry.getInstance();
        if (instance == null) {
            log.warn("Failed execute to get self instance.");
            return MapUtil.empty();
        }
        return convert(instance);
    }

    @Override
    public void changeServerPubModeValue(int grayOrWhiteValue) {
        ServiceInstance instance = registry.getInstance();
        if (instance == null) {
            log.warn("Failed execute to get self instance.");
            return;
        }
        try {
            MetadataInfo metadata = instance.getMetadata();
            metadata.setPubMode(PubMode.of(grayOrWhiteValue));
            registry.update(instance.getApplicationModel());
        } catch (Exception e) {
            log.error("Failed execute to change server pubMode.", e);
        }
    }

    @Override
    public Map<String, Object> getServerSwitcherInfo() {
        Map<Integer, AbstractSwitcher> switchers = GradeSwitcherCenter.getInstance().getActuatorSwitchers();
        HashMap<String, Object> resultMap = MapUtil.newHashMap(switchers.size());
        for (Map.Entry<Integer, AbstractSwitcher> entry : switchers.entrySet()) {
            Integer switcherId = entry.getKey();
            AbstractSwitcher switcher = entry.getValue();
            MicroServerSwitcherInfo switcherInfo = new MicroServerSwitcherInfo(switcher);
            resultMap.put(switcherId.toString(), switcherInfo);
        }
        return resultMap;
    }

    @Override
    public void changeServerSwitcher(MicroServerSwitcherInfo microServerSwitcherInfo) {
        GradeSwitcherCenter.getInstance().updateGradeSwitcherStatus(microServerSwitcherInfo);
        log.info("Change switcher status, id: {} | status: {}.", microServerSwitcherInfo.getId(), microServerSwitcherInfo.getStatus());
        GradeSwitcherListener queryListener = GradeSwitcherListenerRepository.getInstance().queryListener(microServerSwitcherInfo.getId());
        if (queryListener == null) {
            log.warn("Not found switcher listener, id = {}.", microServerSwitcherInfo.getId());
            return;
        }
        queryListener.onGradeChange(microServerSwitcherInfo);
    }

    private Map<String, Object> convert(ServiceInstance instance) {
        HashMap<String, Object> resultMap = MapUtil.newHashMap(8);
        Map<String, String> metadata = instance.getMetadata().getMetadataMap();
        String serviceName = instance.gerServiceName();
        resultMap.put("name", serviceName);
        resultMap.put("alias", MicroServiceConstants.ALIAS_MAP.getOrDefault(serviceName, StringConstants.UNKNOWN));
        resultMap.put(CommonConstants.ACTUATOR_TYPE, metadata.get(CommonConstants.ACTUATOR_TYPE).equals(ActuatorNode.CONSUMER.name()) ? CONSUMER : PROVIDER);
        resultMap.put(CommonConstants.PUB_MODE, metadata.get(CommonConstants.PUB_MODE));
        return resultMap;
    }
}
