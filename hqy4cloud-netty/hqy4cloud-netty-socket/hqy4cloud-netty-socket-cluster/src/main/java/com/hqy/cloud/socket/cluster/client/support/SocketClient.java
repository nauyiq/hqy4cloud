package com.hqy.cloud.socket.cluster.client.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.socket.cluster.client.Client;
import com.hqy.cloud.socket.cluster.client.SocketDirectory;
import com.hqy.cloud.socket.cluster.router.ClusterRouters;
import com.hqy.cloud.socket.cluster.router.ConnectRouterModel;
import com.hqy.cloud.socket.cluster.router.SocketRouter;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import com.hqy.foundation.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * socket客户端视角, 用于获取一个socket连接或者生成一个客户端连接url
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Slf4j
public class SocketClient implements Client {
    private final Registry registry;
    private final AuthorizationService authorizationService;
    private final Map<String, SocketDirectory> directories = new ConcurrentHashMap<>();

    public SocketClient(Registry registry, AuthorizationService authorizationService) {
        this.registry = registry;
        this.authorizationService = authorizationService;
    }

    @Override
    public SocketConnectionInfo getConnection(String application, String bizId) {
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        SocketRouter router = ClusterRouters.router(application);
        // 生成认证参数.
        ConnectRouterModel routerModel = router.getConnectServerModel(bizId, directory.list());
        // 生成认证请求.
        String authorization = authorizationService.getAuthorization(application, bizId);
        // 生成认证请求url
        String connectUrl;
        if (CommonSwitcher.ENABLE_GATEWAY_SOCKET_AUTHORIZE.isOn()) {
            // 如果允许gateway路由socket服务.
            String host = StringConstants.Host.HTTP + IpUtil.getHostAddress();
            connectUrl = ProjectContext.getEnvironment().isDevEnvironment() ?
                    host + StrUtil.COLON + MicroServiceConstants.DEFAULT_PORT_GATEWAY : MicroServiceConstants.DEFAULT_ACCESS_DOMAIN_NAME;
        } else {
            connectUrl = routerModel.socketServer().getAddress();
        }
        return SocketConnectionInfo.of(connectUrl, authorization, routerModel.socketServer().getInfo().getMetadata().getContextPath());
    }

    @Override
    public SocketServer getSocketServer(String application, SocketConnectionInfo info) {
        AssertUtil.notEmpty(application, "Socket server application name should not be empty.");
        AssertUtil.notNull(info, "Socket client connect info should not be null");
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        return directory.getServer(info);
    }

    @Override
    public List<SocketServer> getSocketServers(String application, List<SocketConnectionInfo> infos) {
        AssertUtil.notEmpty(application, "Socket server application name should not be empty.");
        AssertUtil.notEmpty(infos, "Socket client connect info should not be null");
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        return directory.getServers(infos);
    }
}
