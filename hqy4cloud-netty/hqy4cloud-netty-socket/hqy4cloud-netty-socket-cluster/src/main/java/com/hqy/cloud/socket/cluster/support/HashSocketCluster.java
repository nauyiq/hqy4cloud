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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
    private static final String HASH_PARAM = "&hash=";
    private static final int MAX_HASH_TABLE_LENGTH = 64;


    private final HashRouterService hashRouterService;

    public HashSocketCluster(HashRouterService hashRouterService) {
        this.hashRouterService = hashRouterService;
    }

    @Override
    public void init(SocketServer localServer, List<SocketServer> socketServers) {
        String applicationName = localServer.getInfo().getApplicationName();
        Map<Integer, String> allAddress = hashRouterService.getAllAddress(applicationName);
        if (MapUtils.isEmpty(allAddress) || CollectionUtils.isEmpty(socketServers)) {
            hashRouterService.updateHashRoute(applicationName, 0, localServer.getAddress());
        } else {
            socketServers.add(localServer);
            List<String> addressList = socketServers.stream().map(SocketServer::getAddress).toList();
            for (int hash = 0; hash < addressList.size(); hash++) {
                String address = allAddress.get(hash);
                if (StringUtils.isBlank(address) || !addressList.contains(address)) {
                    // 更新路由表.
                    hashRouterService.updateHashRoute(applicationName, hash, localServer.getAddress());
                    return;
                }
            }

        }
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
    protected ConnectBindModel chooseClientBindServer(String bizId, SocketServer localServer, List<SocketServer> socketServers) {
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
        List<SocketServer> reChooseServers = new ArrayList<>(socketServers.size());

        if (StringUtils.isNotBlank(address)) {
            for (SocketServer socketServer : socketServers) {
                String serverAddress = socketServer.getAddress();
                if (address.equals(serverAddress)) {
                    foundServer = socketServer;
                    break;
                } else {
                    if (socketServer.isAvailable()) {
                        reChooseServers.add(socketServer);
                    }
                }
            }
            // 如果服务可用 直接返回服务实例即可.
            if (foundServer != null && foundServer.isAvailable()) {
                return foundServer;
            }
        }

        // 如果允许再次进行hash选择. 开关默认关闭,
        // 如果允许再分配hash，会造成某些服务永远不会被hash到，因为对应的hash值已经被占用。
        // 除非允许重平衡发生，重新路由整个hash表，但是重平衡的前提要断开所有客户端连接，再进行重新分配，代价太大。
        // 而当前hash值如果找不到对应服务， 说明对应服务已经下线或不可用，该hash值已经没有意义，业务层要重新获取socket连接，重新获取新的hash值。
        if (CommonSwitcher.ENABLE_SOCKET_HASH_NOT_FOUND_CHOOSE_AGAIN.isOn() && CollectionUtils.isNotEmpty(reChooseServers)) {
            // 将hash值绑定一个新的地址, 新的地址优先为hash表中未分配的
            Map<Integer, String> allAddress = hashRouterService.getAllAddress(applicationName);
            Collection<String> values = allAddress.values();
            if (values.size() > MAX_HASH_TABLE_LENGTH) {
                // 如果超过hash表允许的最大长度, 则不在进行hash更新， 直接随机选取，并且发出警告..
                log.warn("Socket router hash table too long, application: {}, length: {}.", applicationName, values.size());
                // TODO ALERT NOTICE
                // 随机选取一个节点返回.
                return reChooseServers.get(ThreadLocalRandom.current().nextInt(values.size()));
            }
            // 打乱服务列表顺序
            Collections.shuffle(reChooseServers);
            for (int i = 0; i < reChooseServers.size(); i++) {
                SocketServer socketServer = reChooseServers.get(i);
                String serverAddress = socketServer.getAddress();
                // 判断在hash表中是否存在该服务地址，并且还有其他服务没有选择到的话 则重新选取
                if (values.contains(serverAddress) && i != reChooseServers.size() - 1) {
                    break;
                } else {
                    foundServer = socketServer;
                }
            }

            if (foundServer != null) {
                // 更新一下服务
                hashRouterService.updateHashRoute(applicationName, hash, foundServer.getAddress());
            }
        }
        return foundServer;
    }

    private int getHash(String bizId, List<SocketServer> socketServers) {
        int code = Math.abs(bizId.hashCode());
        return code % socketServers.size();
    }


}
