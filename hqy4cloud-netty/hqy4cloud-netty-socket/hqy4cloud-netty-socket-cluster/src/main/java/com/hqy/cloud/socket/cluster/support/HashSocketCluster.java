package com.hqy.cloud.socket.cluster.support;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.socket.SocketConstants;
import com.hqy.cloud.socket.api.ClientConnection;
import com.hqy.cloud.socket.api.SocketServer;
import com.hqy.cloud.socket.cluster.AbstractSocketCluster;
import com.hqy.cloud.socket.model.SocketConnectionInfo;
import com.hqy.cloud.socket.model.SocketServerInfo;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.router.HashRouterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于hash策略的路由器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
@Slf4j
public class HashSocketCluster extends AbstractSocketCluster {
    public static final String NAME = "HashCluster";
    private static final String HASH_PARAM = "?hash=";

    private final HashRouterService hashRouterService;

    public HashSocketCluster(HashRouterService hashRouterService) {
        this.hashRouterService = hashRouterService;
    }

    @Override
    public String getClusterName() {
        return NAME;
    }

    @Override
    public SocketServer getSocketServer(String applicationName, String bizId, List<SocketServer> socketServers) {
        // 计算hash
        int hash = getHash(bizId, socketServers);
        return findUpdated(applicationName, socketServers, hash);
    }

    @Override
    public Map<String, SocketServer> getSocketServers(String applicationName, Set<String> bizIdSet, List<SocketServer> socketServers) {
        Map<String, Integer> biaIdHashMap = bizIdSet.stream().collect(Collectors.toMap(Function.identity(), bizId -> getHash(bizId, socketServers)));
        Map<Integer, String> addressMap = this.hashRouterService.getAddress(applicationName, new HashSet<>(biaIdHashMap.values()));
        Map<String, SocketServer> result = new HashMap<>(bizIdSet.size());
        for (String bizId : bizIdSet) {
            Integer hash = biaIdHashMap.get(bizId);
            String address = addressMap.get(hash);
            SocketServer server = getSocketServer(applicationName, socketServers, hash, address);
            if (server == null) {
                result.put(bizId, null);
                continue;
            }
            if (StringUtils.isBlank(address)) {
                addressMap.put(hash, server.getAddress());
            }
            result.put(bizId, server);
        }
        return result;
    }

    @Override
    protected SocketServer doFind(List<SocketServer> socketServers, SocketConnectionInfo connectionInfo) {
        // 获取连接的应用名
        String applicationName = socketServers.get(0).getInfo().getApplicationName();
        HashClientConnection connection = (HashClientConnection) connectionInfo.getConnection();
        return findUpdated(applicationName, socketServers, connection.getHash());
    }


    @Override
    protected List<SocketServer> doFind(List<SocketServer> socketServers, List<SocketConnectionInfo> connectionInfos) {
        // 获取连接的应用名
        String applicationName = socketServers.get(0).getInfo().getApplicationName();
        // 获取客户端进行连接的hash参数值.
        Set<Integer> hashSet = connectionInfos.stream().map(connectionInfo -> connectionInfo.getParameter(SocketConstants.SOCKET_CLUSTER_ROUTER_HASH_KEY, 0)).collect(Collectors.toSet());
        // 获取hash值对应的路由地址.
        Map<Integer, String> hashAddress = hashRouterService.getAddress(applicationName, hashSet);
        List<SocketServer> results = new ArrayList<>(connectionInfos.size());
        for (SocketConnectionInfo connectionInfo : connectionInfos) {
            HashClientConnection connection = (HashClientConnection) connectionInfo.getConnection();
            int hash = connection.getHash();
            String address = hashAddress.get(hash);
            SocketServer socketServer = getSocketServer(applicationName, socketServers, hash, address);
            if (socketServer == null) {
                results.add(null);
            } else {
                if (StringUtils.isBlank(address)) {
                    hashAddress.put(hash, socketServer.getAddress());
                }
                results.add(socketServer);
            }
        }
        return results;
    }

    @Override
    protected ConnectBindModel chooseClientBindServer(String bizId, List<SocketServer> socketServers) {
        int hash = getHash(bizId, socketServers);
        SocketServer socketServer = findUpdated(socketServers.get(0).getInfo().getApplicationName(), socketServers, hash);
        SocketServerInfo info = socketServer.getInfo();
        Map<String, String> params = new HashMap<>(2);
        params.put(SocketConstants.SOCKET_CLUSTER_ROUTER_HASH_KEY, Integer.toString(hash));
        return ConnectBindModel.of(socketServer, params);
    }

    @Override
    protected ClientConnection genConnection(ConnectBindModel routerModel, String connectUrl, String authorization, String contextPath) {
        String parameter = routerModel.getParameter(SocketConstants.SOCKET_CLUSTER_ROUTER_HASH_KEY);
        AssertUtil.notEmpty(parameter, "Connection model hash is null.");
        int hash = Integer.parseInt(parameter);
        connectUrl = connectUrl + HASH_PARAM + hash;
        return new HashClientConnection(connectUrl, authorization, contextPath, hash);
    }

    private SocketServer findUpdated(String applicationName, List<SocketServer> socketServers, int hash) {
        String address = this.hashRouterService.getAddress(applicationName, hash);
        return getSocketServer(applicationName, socketServers, hash, address);
    }

    private SocketServer getSocketServer(String applicationName, List<SocketServer> socketServers, int hash, String address) {
        boolean update = false;
        SocketServer foundServer = null;
        if (StringUtils.isBlank(address)) {
            if (CommonSwitcher.ENABLE_SOCKET_HASH_NOT_FOUND_CHOOSE_AGAIN.isOn()) {
                update = true;
                foundServer = socketServers.get(ThreadLocalRandom.current().nextInt(socketServers.size()));
                address = foundServer.getAddress();
            } else {
                return null;
            }
        } else {
            for (SocketServer socketServer : socketServers) {
                String serverAddress = socketServer.getAddress();
                if (address.equals(serverAddress)) {
                    foundServer = socketServer;
                    break;
                }
            }

            if (foundServer == null) {
                if (CommonSwitcher.ENABLE_SOCKET_HASH_NOT_FOUND_CHOOSE_AGAIN.isOn()) {
                    update = true;
                    foundServer = socketServers.get(ThreadLocalRandom.current().nextInt(socketServers.size()));
                    address = foundServer.getAddress();
                } else {
                    return null;
                }
            }

        }

        if (update) {
            hashRouterService.updateHashRoute(applicationName, hash, address);
        }

        return foundServer;
    }

    private int getHash(String bizId, List<SocketServer> socketServers) {
        int code = Math.abs(bizId.hashCode());
        return code % socketServers.size();
    }


}
