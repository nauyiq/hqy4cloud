package com.hqy.cloud.socket.cluster.client.support;

import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.cluster.client.Client;
import com.hqy.cloud.socket.cluster.client.SocketDirectory;
import com.hqy.cloud.socket.cluster.SocketCluster;
import com.hqy.cloud.socket.cluster.support.SocketClusters;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final Map<String, SocketDirectory> directories = new ConcurrentHashMap<>();

    public SocketClient(Registry registry) {
        this.registry = registry;
    }

    @Override
    public SocketServer getSocketServer(String application, SocketConnectionInfo info) {
        AssertUtil.notEmpty(application, "Socket server application name should not be empty.");
        AssertUtil.notNull(info, "Socket client connect info should not be null");
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        return directory.getServer(info);
    }

    @Override
    public SocketServer findSocketServer(String application, String bizId) {
        AssertUtil.notEmpty(application, "Socket server application name should not be empty.");
        AssertUtil.notEmpty(bizId, "BizId should not be empty.");
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        SocketCluster router = SocketClusters.cluster(directory.getClusterType());
        return router.getSocketServer(application, bizId, directory.list());
    }

    @Override
    public Map<String, SocketServer> findSocketServers(String application, Set<String> bizIds) {
        AssertUtil.notEmpty(application, "Socket server application name should not be empty.");
        AssertUtil.notEmpty(bizIds, "BizId set should not be empty.");
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        SocketCluster router = SocketClusters.cluster(directory.getClusterType());
        return router.getSocketServers(application, bizIds, directory.list());
    }

    @Override
    public List<SocketServer> getSocketServers(String application, List<SocketConnectionInfo> infos) {
        AssertUtil.notEmpty(application, "Socket server application name should not be empty.");
        AssertUtil.notEmpty(infos, "Socket client connect info should not be null");
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        return directory.getServers(infos);
    }

    @Override
    public List<SocketServer> getAllSocketServer(String application) {
        AssertUtil.notEmpty(application, "Socket server application name should not be empty.");
        SocketDirectory directory = directories.computeIfAbsent(application, v -> new DynamicSocketDirectory(application, registry));
        return directory.list();
    }
}
