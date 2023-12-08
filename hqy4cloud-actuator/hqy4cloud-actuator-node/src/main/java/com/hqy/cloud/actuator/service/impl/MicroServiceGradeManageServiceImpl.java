package com.hqy.cloud.actuator.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.actuator.core.GradeSwitcherListener;
import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;
import com.hqy.cloud.actuator.server.GradeSwitcherCenter;
import com.hqy.cloud.actuator.server.GradeSwitcherListenerRepository;
import com.hqy.cloud.actuator.service.MicroServiceGradeManageService;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.AbstractSwitcher;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.nacos.discovery.NacosDiscovery;
import com.hqy.cloud.rpc.nacos.naming.NamingServiceWrapper;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
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
    private final NacosDiscovery nacosDiscovery;

    @Override
    public Map<String, Object> getServerGradeInfo() {
        Instance selfInstance = nacosDiscovery.getSelfInstance();
        if (selfInstance == null) {
            log.warn("Failed execute to get self instance.");
            return MapUtil.empty();
        }
        return convert(selfInstance);
    }

    @Override
    public void changeServerPubModeValue(int grayOrWhiteValue) {
        Instance selfInstance = nacosDiscovery.getSelfInstance();
        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();
        if (selfInstance == null) {
            log.error("Can not found self instance. info: {}.", JsonUtil.toJson(contextInfo.getUip()));
            return;
        }
        NamingServiceWrapper wrapper = nacosDiscovery.getServiceWrapper();
        try {
            Map<String, String> metadata = selfInstance.getMetadata();
            metadata.put(CommonConstants.PUB_MODE, grayOrWhiteValue + "");
            wrapper.updateInstance(contextInfo.getNameEn(), selfInstance);
        } catch (NacosException e) {
            log.error("Failed execute to change server pubMode.", e);
        }
    }

    @Override
    public Map<String, Object> getServerSwitcherInfo() {
        Map<Integer, AbstractSwitcher> switchers = GradeSwitcherCenter.getInstance().getSwitchers();
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

    private Map<String, Object> convert(Instance instance) {
        HashMap<String, Object> resultMap = MapUtil.newHashMap(8);
        Map<String, String> metadata = instance.getMetadata();
        String serviceName = instance.getServiceName();
        String[] split = serviceName.split(StringConstants.Symbol.AT_AT);
        if (split.length == 2) {
            resultMap.put(CommonConstants.GROUP, split[0]);
            serviceName = split[1];
        }
        resultMap.put("name", serviceName);
        resultMap.put("alias", MicroServiceConstants.ALIAS_MAP.getOrDefault(serviceName, StringConstants.UNKNOWN));
        resultMap.put(CommonConstants.ACTUATOR_TYPE, metadata.get(CommonConstants.ACTUATOR_TYPE).equals(ActuatorNode.CONSUMER.name()) ? CONSUMER : PROVIDER);
        resultMap.put(CommonConstants.PUB_MODE, metadata.get(CommonConstants.PUB_MODE));
        return resultMap;
    }
}
