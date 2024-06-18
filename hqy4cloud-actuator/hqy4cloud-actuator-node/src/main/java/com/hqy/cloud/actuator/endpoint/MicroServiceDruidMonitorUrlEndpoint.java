package com.hqy.cloud.actuator.endpoint;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.actuator.core.Indicator;
import com.hqy.cloud.common.base.config.ConfigConstants;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.authentication.AuthenticationRequestContext;
import com.hqy.cloud.common.base.project.ProjectContextInfo;
import com.hqy.cloud.util.web.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/25 15:06
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Endpoint(id = MicroServiceDruidMonitorUrlEndpoint.ID)
public class MicroServiceDruidMonitorUrlEndpoint implements Indicator<String> {
    public static final String ID = "druid";
    private static final String URL_KEY = "url";
    private static final String ENABLED_KEY = "enabled";

    private final Environment environment;

    @ReadOperation
    public Map<String, Object> readEndpoint() {
        Map<String, Object> result = MapUtil.newHashMap(1);
        try {
            // 获取客户端IP
            HttpServletRequest request = RequestUtil.currentRequest();
            String requestIp = IpUtil.getRequestIp(request);
            boolean enabled = environment.getProperty(ConfigConstants.DRUID_MONITOR_ENABLED_KEY, Boolean.class, false) ||
                    environment.getProperty(ConfigConstants.DRUID_MONITOR_ALLOW_KEY, StrUtil.EMPTY).contains(requestIp);
            if (enabled) {
                // 尝试获取暴露外部的IP
                String discovery = environment.getProperty(ConfigConstants.DISCOVERY_IP, String.class);
                // 获取访问druid连接池监控页面的url
                String url = StringUtils.isBlank(discovery) ? getLocalUrl() : getDiscoveryUrl(discovery);
                // 获取basic认证
                String basicAuth = getBasicAuth();
                if (StringUtils.isNotBlank(basicAuth)) {
                    url = url.concat(StringConstants.Symbol.QUESTION_MARK).concat(HttpHeaders.AUTHORIZATION).concat(StringConstants.Symbol.EQUALS).concat(basicAuth);
                }
                result.put(URL_KEY, url);
            }
            result.put(ENABLED_KEY, enabled);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }
        return result;
    }

    private String getBasicAuth() {
        String username = environment.getProperty(ConfigConstants.SPRING_BOOT_ADMIN_CLIENT_USERNAME);
        String password = environment.getProperty(ConfigConstants.SPRING_BOOT_ADMIN_CLIENT_PASSWORD);
        if (StringUtils.isAnyBlank(username, password)) {
            return StrUtil.EMPTY;
        }
        return AuthenticationRequestContext.buildBasicAuth(username, password);
    }

    @Override
    public String indicatorId() {
        return ID;
    }

    private String getLocalUrl() {
        ProjectContextInfo info = ProjectContext.getContextInfo();
        UsingIpPort uip = info.getUip();
        return StringConstants.Host.HTTP + IpUtil.getHostAddress() + StrUtil.COLON + uip.getPort() + StrUtil.SLASH + ID;
    }

    private String getDiscoveryUrl(String discovery) {
        return StringConstants.Host.HTTP + discovery + StrUtil.SLASH + ID;
    }
}
