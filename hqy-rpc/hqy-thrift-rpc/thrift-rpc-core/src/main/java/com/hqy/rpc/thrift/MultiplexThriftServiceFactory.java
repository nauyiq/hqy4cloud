/*
package com.hqy.rpc.thrift;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NiftyClientChannel;
import com.google.common.net.HostAndPort;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.base.common.exception.NoAvailableProvidersException;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

*/
/**
 * 复合的ThriftService工厂类
 * 利用apache common pool ，对象缓存复用。+NIO链接多路复用。<br>
 * 通过暴露的updateAddresses 方法 来更新远程连接。<br>
 * <T> RPCService
 * @author qy
 * @date  2021-08-13 14:43
 *//*

public class MultiplexThriftServiceFactory<T> extends BasePooledObjectFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(MultiplexThriftServiceFactory.class);

    */
/**
     * RPCService
     *//*

    private final Class<T> serviceClass;

    */
/**
     * 计数
     *//*

    private final static AtomicInteger CREATE_COUNT = new AtomicInteger(0);

    */
/**
     * thrift netty 客户端连接器 所有颜色
     *//*

    private FramedClientConnector[] connectorsAll;

    */
/**
     * thrift netty 客户端连接器 灰度
     *//*

    private FramedClientConnector[] connectorsGray;

    */
/**
     * thrift netty 客户端连接器 白度
     *//*

    private FramedClientConnector[] connectorsWhite;

    */
/**
     * thrift netty 客户端连接器 同ip同环卡
     *//*

    private FramedClientConnector[] connectorsHighPriority;

    */
/**
     * 本机ip
     *//*

    private final String hostIp;

    */
/**
     * 可重入锁：多线程环境下（事件异步通知可用服务节点变化），保障 connectors 线程安全
     *//*

    private final ReentrantLock lock = new ReentrantLock();

    public MultiplexThriftServiceFactory(Class<T> serviceClass, List<UsingIpPort> addressGray,
                                         List<UsingIpPort> addressWhite, String hostIp) {
        this.serviceClass = serviceClass;
        this.hostIp = hostIp;
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("[ThriftServiceFactory] init! addressesGray={}", addressGray);
            log.debug("[ThriftServiceFactory] init! addressesWhite={}", addressWhite);
            log.debug("[ThriftServiceFactory] init! hostIp={}", hostIp);
        }
        updateAddress(addressGray, addressWhite);
    }


    public String gerServiceInfo(T service) {
        NiftyClientChannel clientChannel = getClientChannel(service);
        if (Objects.isNull(clientChannel)) {
            return String.format("Invalid service: [%s], NiftyClientChannel is null", service.getClass().getSimpleName());
        }
        return toChannelString(clientChannel);
    }


    */
/**
     * 设定或者刷新 远程可用链接。
     * @param addressGray 灰度可连接列表
     * @param addressWhite 白度可连接列表
     *//*

    public void updateAddress(List<UsingIpPort> addressGray, List<UsingIpPort> addressWhite) {
        if(CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("[ENABLE_GRAY_MECHANISM]updateAddresses: usable addressesGray size={}, addressesWhite size={}",
                    addressGray.size(), addressWhite.size());
        }
        //创建灰色的连接列表 客户端连接器
        createFramedClientConnector(addressGray, GrayWhitePub.GRAY);
        //创建白色的连接列表 客户端连接器
        createFramedClientConnector(addressWhite, GrayWhitePub.WHITE);

        List<UsingIpPort> all = new ArrayList<>(addressGray);
        for (UsingIpPort usingIpPort : addressWhite) {
            if (!all.contains(usingIpPort)) {
                all.add(usingIpPort);
            }
        }
        //创建所有颜色的连接列表 客户端连接器
        createFramedClientConnector(addressWhite, GrayWhitePub.NONE);

        if (CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOn()) {
            List<UsingIpPort> sameIpList = new ArrayList<>();
            for (UsingIpPort usingIpPort : all) {
                if (usingIpPort.getHostAddr().equals(hostIp)) {
                    sameIpList.add(usingIpPort);
                }
            }
            if (CollectionUtils.isNotEmpty(sameIpList)) {
                //创建同ip同环卡的连接列表 客户端连接器
                createFramedClientConnector(sameIpList, GrayWhitePub.HIGH);
            } else {
                log.info("@@@ Not find sameIpList , oops ~");
                this.connectorsHighPriority = new FramedClientConnector[0];
            }

        } else {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.info("@@@ CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOff !");
            }
        }

    }

    @Override
    public T create() throws Exception {
        FramedClientConnector connector;
        //根据当前节点颜色 和有无启用灰度策略 找出合适地连接数组
        FramedClientConnector[] connectors = findSuitableConnectors();
        lock.lock();
        try {
            if (connectors.length == 0) {
                throw new NoAvailableProvidersException("No available connectors for create pool object.");
            }
            int idx = CREATE_COUNT.incrementAndGet() % connectors.length;
            connector = connectors[idx];
        } finally {
            lock.unlock();
            //防止整形溢出！！
            if(CREATE_COUNT.get() == BaseMathConstants.POINTER){
                CREATE_COUNT.set(0);
            }
        }

        if(CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("create new client, connector:{}", connector);
        }
        //创建Thrift netty长连接客户端
        return MultiplexThriftClientManager.getThriftClientManager().createClient(connector, serviceClass).get();
    }

    @Override
    public PooledObject<T> wrap(T t) {
        return new DefaultPooledObject<>(t);
    }

    @Override
    public void destroyObject(PooledObject<T> p) {
        NiftyClientChannel clientChannel = getClientChannel(p.getObject());
        if (Objects.isNull(clientChannel)) {
            return;
        }
        if (clientChannel.getNettyChannel().isConnected()) {
            clientChannel.close();
        }
        //关闭一个Channel
        log.info("[destroyObject] channel:{}", toChannelString(clientChannel));
    }


    private String toChannelString(NiftyClientChannel clientChannel) {
        if (Objects.isNull(clientChannel)) {
            return "";
        }
        Channel channel = clientChannel.getNettyChannel();
        return String.format("(%s - > %s)", channel.getLocalAddress(), channel.getRemoteAddress());
    }

    */
/**
     * 获取netty客户端通道
     * @param object
     * @return
     *//*

    private NiftyClientChannel getClientChannel(T object) {
        try {
            return (NiftyClientChannel)MultiplexThriftClientManager.getThriftClientManager().getRequestChannel(object);
        } catch (Exception e) {
            log.error("[ENABLE_GRAY_MECHANISM][getClientChannel] failed.", e);
        }
        return null;
    }

    */
/**
     * 根据当前节点颜色 和有无启用灰度策略 找出合适地连接数组
     * @return
     *//*

    private FramedClientConnector[] findSuitableConnectors() {
        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();
        if (CommonSwitcher.ENABLE_GRAY_MECHANISM.isOn()) {
            //是否启用灰度机制
            if (contextInfo.getPubValue().equals(GrayWhitePub.GRAY.value)) {
                return connectorsGray;
            }
            if (contextInfo.getPubValue().equals(GrayWhitePub.WHITE.value)) {
                return connectorsWhite;
            }
        } else {
            boolean flag = CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOn() || (connectorsHighPriority != null && connectorsHighPriority.length > 0);
            if (flag) {
                if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                    log.info("### 非灰度模式下优先使用 同IP的服务节点:YES ! {} ", hostIp);
                }
                return connectorsHighPriority;
            }
            return connectorsAll;
        }

        throw new IllegalStateException("内部状态错误，灰度机制不清晰！Internal error ！");
    }


    */
/**
     * 创建thrift netty 客户端连接器
     * @param usingIpPorts 节点ip列表
     * @param pub 颜色
     *//*

    private void createFramedClientConnector(List<UsingIpPort> usingIpPorts, GrayWhitePub pub) {
        //所有颜色的
        lock.lock();
        try {
            if (pub.value == GrayWhitePub.NONE.value) {
                connectorsAll = new FramedClientConnector[usingIpPorts.size()];
                int i = 0;
                for (UsingIpPort usingIpPort : usingIpPorts) {
                    HostAndPort hostAndPort = HostAndPort.fromParts(usingIpPort.getHostAddr(), usingIpPort.getRpcPort());
                    this.connectorsAll[i] = new FramedClientConnector(hostAndPort);
                    i++;
                }
            }
        } finally {
            lock.unlock();
        }
        //白色的
        lock.lock();
        try {
            if (pub.value == GrayWhitePub.WHITE.value) {
                connectorsWhite = new FramedClientConnector[usingIpPorts.size()];
                int i = 0;
                for (UsingIpPort usingIpPort : usingIpPorts) {
                    HostAndPort hostAndPort = HostAndPort.fromParts(usingIpPort.getHostAddr(), usingIpPort.getRpcPort());
                    this.connectorsWhite[i] = new FramedClientConnector(hostAndPort);
                }
            }
        } finally {
            lock.unlock();
        }
        //灰色的
        lock.lock();
        try {
            if (pub.value == GrayWhitePub.GRAY.value) {
                connectorsGray = new FramedClientConnector[usingIpPorts.size()];
                int i = 0;
                for (UsingIpPort usingIpPort : usingIpPorts) {
                    HostAndPort hostAndPort = HostAndPort.fromParts(usingIpPort.getHostAddr(), usingIpPort.getRpcPort());
                    this.connectorsGray[i] = new FramedClientConnector(hostAndPort);
                }
            }
        } finally {
            lock.unlock();
        }
        //同环卡
        lock.lock();
        try {
            if (pub.value == GrayWhitePub.HIGH.value) {
                connectorsHighPriority = new FramedClientConnector[usingIpPorts.size()];
                int i = 0;
                for (UsingIpPort usingIpPort : usingIpPorts) {
                    HostAndPort hostAndPort = HostAndPort.fromParts(usingIpPort.getHostAddr(), usingIpPort.getRpcPort());
                    this.connectorsHighPriority[i] = new FramedClientConnector(hostAndPort);
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
*/
