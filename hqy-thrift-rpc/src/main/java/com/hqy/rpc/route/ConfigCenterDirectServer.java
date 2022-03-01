package com.hqy.rpc.route;

import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于@RreshScope注解获取配置中心的配置数据
 * @author qiyuan.hong
 * @date 2022-02-25 22:47
 */
@Component
public class ConfigCenterDirectServer implements DirectModuleManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigCenterDirectServer.class);

    /**
     * key:服务名
     * value ip:端口 比如172.0.0.1:8080
     */
//    @Value("#{${direct.nodesMap}}")
    private static Map<String, String> directNodesMap = new HashMap<>();
    static {
        directNodesMap.put("test", "test");
    }

    private boolean checkNodes() {
        if (directNodesMap == null || directNodesMap.isEmpty()) {
            log.warn("@@@ Initialize directNodes, directNodes is null.");
            return false;
        }
        return true;
    }

    @Override
    public UsingIpPort getDirectUip(String moduleNameEn) {
        boolean result = checkNodes();
        if (!result) {
            return null;
        }
        String ipPort = directNodesMap.get(moduleNameEn);
        if (StringUtils.isBlank(ipPort)) {
            return null;
        }
        return ThriftRpcHelper.convertHash(ipPort);
    }

    @Override
    public boolean isDirect(String moduleName) {
        if (StringUtils.isBlank(moduleName)) {
            return false;
        }
        boolean result = checkNodes();
        if (result) {
            return directNodesMap.get(moduleName) != null;
        }
        return false;
    }
}
