package com.hqy.rpc.thrift;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NiftyClientChannel;
import com.google.common.net.HostAndPort;
import com.hqy.fundation.common.exception.NoAvailableProvidersException;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.fundation.concurrent.ThreadLocalPool;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 利用apache common pool ，对象缓存复用。+NIO链接多路复用。<br>
 *  * 通过暴露的updateAddresses 方法 来更新远程连接。<br>
 *  <T> RPCService
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 14:43
 */
@Slf4j
public class MultiplexThriftServiceFactory<T> extends BasePooledObjectFactory<T> {

    private final Class<T> serviceClass;

    private final static AtomicInteger createCount = new AtomicInteger(0);

    private FramedClientConnector[] connectorsAll;

    private FramedClientConnector[] connectorsGray;

    private FramedClientConnector[] connectorsWhite;

    private FramedClientConnector[] connectorsHighPriority;

    private final String hostIp;

    /**
     * 可重入锁：多线程环境下（事件异步通知可用服务节点变化），保障 connectors 线程安全
     */
    private final ReentrantLock lock = new ReentrantLock();

    public MultiplexThriftServiceFactory(Class<T> serviceClass, List<UsingIpPort> addressGray,
                                         List<UsingIpPort> addressWhite, String hostIp) {
        this.serviceClass = serviceClass;
        this.hostIp = hostIp;
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("[ThriftServiceFactory] init ! addressesGray={}", addressGray);
            log.debug("[ThriftServiceFactory] init ! addressesWhite={}", addressWhite);
            log.debug("[ThriftServiceFactory] init ! hostIp={}", hostIp);
        }
        updateAddress(addressGray, addressWhite);
    }

    /**
     * 设定或者刷新 远程可用链接。
     * @param addressGray
     * @param addressWhite
     */
    private void updateAddress(List<UsingIpPort> addressGray, List<UsingIpPort> addressWhite) {
        if(CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("[ENABLE_GRAY_MECHANISM]updateAddresses: usable addressesGray size={}, addressesWhite size={}", addressGray.size(), addressWhite.size());
        }

        try {
            lock.lock();
            connectorsGray = new FramedClientConnector[addressGray.size()];
            for (int i = 0; i < addressGray.size(); i++) {
                UsingIpPort usingIpPort = addressGray.get(i);
                HostAndPort hostAndPort = HostAndPort.fromParts(usingIpPort.getIp(), usingIpPort.getPort());
                this.connectorsGray[i] = new FramedClientConnector(hostAndPort);
            }
        } finally {
            lock.unlock();
        }

        try {
            lock.lock();
            connectorsWhite = new FramedClientConnector[addressWhite.size()];
            for (int i = 0; i < addressWhite.size(); i++) {
                UsingIpPort usingIpPort = addressWhite.get(i);
                HostAndPort hostAndPort =  HostAndPort.fromParts(usingIpPort.getIp(), usingIpPort.getPort());
                this.connectorsWhite[i] = new FramedClientConnector(hostAndPort);
            }
        } finally {
            lock.unlock();
        }

        List<UsingIpPort> all = new ArrayList<>(addressGray);
        for (UsingIpPort usingIpPort : addressWhite) {
            if (!all.contains(usingIpPort)) {
                all.add(usingIpPort);
            }
        }

        try {
            lock.lock();
            connectorsAll = new FramedClientConnector[all.size()];
            for (int i = 0; i < all.size(); i++) {
                UsingIpPort usingIpPort = all.get(i);
                HostAndPort hostAndPort = HostAndPort.fromParts(usingIpPort.getIp(), usingIpPort.getPort());
                this.connectorsAll[i] = new FramedClientConnector(hostAndPort);
            }
        } catch (Exception e) {
            lock.unlock();
        }

        try {
            lock.lock();
            if (CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOn()) {
                List<UsingIpPort> sameIpAddrList = new ArrayList<>();
                for (UsingIpPort usingIpPort : all) {
                    if (usingIpPort.getIp().equals(hostIp)) {
                        sameIpAddrList.add(usingIpPort);
                    }
                }

                if (CollectionUtils.isNotEmpty(sameIpAddrList)) {
                    this.connectorsHighPriority = new FramedClientConnector[sameIpAddrList.size()];
                    for (int i = 0; i < sameIpAddrList.size(); i++) {
                        UsingIpPort usingIpPort = sameIpAddrList.get(i);
                        HostAndPort hostAndPort = HostAndPort.fromParts(usingIpPort.getIp(), usingIpPort.getPort());
                        this.connectorsHighPriority[i] = new FramedClientConnector(hostAndPort);
                    }
                } else {
                    log.info("Not find sameIpAddrList , oops ~");
                    this.connectorsHighPriority = new FramedClientConnector[0];
                }

            } else {
                if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                    log.info("CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOff !");
                }
            }
        } finally {
            lock.unlock();
        }
    }


    @Override
    public T create() throws Exception {
        FramedClientConnector connector;
        //根据当前节点颜色 和有无启用灰度策略 找出合适的连接数组
        FramedClientConnector[] connectors = findSuitableConnectors();
        try {
            lock.lock();
            if (connectors.length == 0) {
                throw new NoAvailableProvidersException("No available connectors for create pool object.");
            }
            int idx = createCount.incrementAndGet() % connectors.length;
            connector = connectors[idx];
            ThreadLocalPool.RPC_CONNECTOR_ADDRESS.set(connector.toString());
        } finally {
            lock.unlock();
            //防止整形溢出！！
            if(createCount.get() == 1000000000){
                createCount.set(0);
            }
        }

        if(CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("create new client, connector:{}", connector);
        }
        return MultiplexThriftClientManager.getThriftClientManager().createClient(connector, serviceClass).get();
    }

    @Override
    public PooledObject<T> wrap(T t) {
        return new DefaultPooledObject<>(t);
    }

    @Override
    public void destroyObject(PooledObject<T> p) throws Exception {
        NiftyClientChannel clientChannel = getClientChannel(p.getObject());
        if (Objects.isNull(clientChannel)) return;
        if (clientChannel.getNettyChannel().isConnected()) {
            clientChannel.close();
        }
        //关闭一个Channel
        log.info("[destroyObject] channel:{}", toChannelString(clientChannel));
    }

    private String toChannelString(NiftyClientChannel clientChannel) {
        Channel channel = clientChannel.getNettyChannel();
        return String.format("(%s - > %s)", channel.getLocalAddress(), channel.getRemoteAddress());
    }

    private NiftyClientChannel getClientChannel(T object) {
        try {
            return (NiftyClientChannel)MultiplexThriftClientManager.getThriftClientManager().getRequestChannel(object);
        } catch (Exception e) {
            log.error("[ENABLE_GRAY_MECHANISM][getClientChannel] failed.", e);
        }
        return null;
    }

    private FramedClientConnector[] findSuitableConnectors() {
        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();
        if (CommonSwitcher.ENABLE_GRAY_MECHANISM.isOn()) { //是否启用灰度机制
            if (contextInfo.getPubValue().equals(GrayWhitePub.GRAY.value)) {
                return connectorsGray;
            }
            if (contextInfo.getPubValue().equals(GrayWhitePub.WHITE.value)) {
                return connectorsWhite;
            }
        } else {
            if (CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOn() || (connectorsHighPriority != null && connectorsHighPriority.length > 0)) {
                if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                    log.info("### 非灰度模式下优先使用 同IP的服务节点:YES ! {} ", hostIp);
                }
                return connectorsHighPriority;
            }
            return connectorsAll;
        }

        throw new IllegalStateException("内部状态错误，灰度机制不清晰！Internal error ！");
    }
}
