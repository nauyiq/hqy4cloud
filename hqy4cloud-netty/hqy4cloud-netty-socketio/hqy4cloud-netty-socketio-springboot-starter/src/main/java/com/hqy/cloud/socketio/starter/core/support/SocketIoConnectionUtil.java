package com.hqy.cloud.socketio.starter.core.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.config.ConfigurationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.hqy.cloud.common.base.config.ConfigConstants.SOCKET_CONNECTION_HOST;
import static com.hqy.cloud.util.config.ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 13:44
 */
@Slf4j
public class SocketIoConnectionUtil {

    public static String getSocketHost(int port) {
        String host = ConfigurationContext.getProperty(SERVER_PROPERTIES, SOCKET_CONNECTION_HOST);
        if (StringUtils.isBlank(host)) {
            if (StringUtils.isBlank(host) && Environment.getInstance().isDevEnvironment()) {
                // Dev env using ip.
                host = StringConstants.Host.HTTP + IpUtil.getHostAddress();
                host = CommonSwitcher.ENABLE_GATEWAY_SOCKET_AUTHORIZE.isOn() ? host + StrUtil.COLON + 9527 : host + StrUtil.COLON + port;
                return host;
            } else {
                return StringConstants.Host.HTTPS_API_GATEWAY;
            }
        }
        return host + StrUtil.COLON + port;

    }



}
