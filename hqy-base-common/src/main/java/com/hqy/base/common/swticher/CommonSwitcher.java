package com.hqy.base.common.swticher;

/**
 * 通用的节点开关
 * @author qy
 * @date 2021-07-28 9:35
 */
public class CommonSwitcher extends AbstractSwitcher {

    private static final long serialVersionUID = -5097824901579636026L;

    protected CommonSwitcher(int id, String name, boolean status) {
        super(id, name, status);
    }

    /**
     * 节点-是否启用灰度机制？启用后 需要区分灰度与白度，否则不区分
     */
    public static final CommonSwitcher ENABLE_GRAY_MECHANISM = new CommonSwitcher(200,"节点-是否启用灰度机制",false);

    /**
     * 节点-是否启用共享的BIBlockIP清单（Redis）;如果false 各个项目自行维护本地缓存的BI的ip禁止名单。
     */
    public static final CommonSwitcher ENABLE_SHARED_BLOCK_IP_LIST = new CommonSwitcher(201,"节点-是否启用共享的BIBlockIP清单（Redis）",true);

    /**
     * 节点-是否采用基于Lettuce的RedisTemplate （默认true）
     */
    public static final CommonSwitcher ENABLE_LETTUCE_REDIS_TEMPLATE = new CommonSwitcher(202, "节点-是否采用基于Lettuce的RedisTemplate", true);

    /**
     * 节点-同IP/Env RPC调度优先(非灰度机制才有效)
     */
    public static final CommonSwitcher ENABLE_RPC_SAME_IP_HIGH_PRIORITY = new CommonSwitcher(207,"节点-同IP/Env RPC调度优先(非灰度机制才有效)",true);

    /**
     * 节点-是否支持泛型RPC-MSG通道（消息通道）
     */
    public static final CommonSwitcher ENABLE_MSG_CHANNEL_4_GENERIC_RPC = new CommonSwitcher(208, "节点-是否支持泛型RPC-MSG通道（消息通道）", false);


    /**
     * 节点-使用ThriftServer-Bean, 标志为RPC的提供者
     */
    public static final CommonSwitcher ENABLE_THRIFT_SERVER_BEAN = new CommonSwitcher(210, "节点-使用ThriftServer-Bean, 标志为RPC的提供者", true);

    /**
     * 节点-是否采用ExceptionCollActionEventHandler
     */
    public static final CommonSwitcher ENABLE_EXCEPTION_COLL_ACTION_EVENT_HANDLER = new CommonSwitcher(210, "节点-是否采用ExceptionCollActionEventHandler", true);

    /**
     * 节点-框架socket-polling环节泄漏HandshakeData兼容，默认测试环境开启.
     * 场景一： 网络原因，没升级到websocket
     * 场景二：恶意的polling的操作 导致的 addClient的uuidMap 泄漏
     */
    public static final CommonSwitcher SOCKET_POLLING_HANDSHAKE_DATA_LEAK = new CommonSwitcher(211, "节点-框架socket-polling环节泄漏HandshakeData监听，默认开启", true);

    /**
     * 节点-是否启用--框架 检测 allClients的内存泄漏
     */
    public static final CommonSwitcher ENABLE_NAMESPACE_CLIENTS_LEAK_PROTECTION = new CommonSwitcher(212, "节点-是否启用Namespace Clients内存泄露保护开关", true);


    /**
     * 节点-是否由nginx反向代理 提供SSL链接?
     */
    public static final CommonSwitcher CONFIG_SSL_BY_NGINX_PROXY = new CommonSwitcher(213,"节点-是否由nginx反向代理 提供SSL链接", true);

    /**
     * 与sid有关（重联场景是生成新的sid)
     * 无用http长连接释放（短时间泄漏）
     * 场景：业务： 兼容忽略js前端框架层面的socketIO层面 多发一次无意义请求的返回开关   <br>
     * 场景：技术：因js和netty均是异步线程，状态不一致性的情况。即潜在逻辑：建立完101、以及收到 NOOP(6)之后，多发的一次Polling长连接<br>
     */
    public static final CommonSwitcher SOCKET_POLLING_HTTP_LEAK = new CommonSwitcher(244, "节点-框架 前端多发无用http长连接释放，默认[关]", false);

    /**
     * 节点-测试开关（仅仅用于开关测试，不要用于业务规则判定）
     */
    public static final CommonSwitcher JUST_4_TEST_DEBUG = new CommonSwitcher(250,"节点-DEBUG开关（默认关）",false);

    /**
     * 节点-是否采用上下文里的白名单
     */
    public static final CommonSwitcher ENABLE_PROJECT_CONTEXT_WHITE = new CommonSwitcher(251, "节点-是否采用上下文里的白名单", true);

    /**
     * 节点-是否启用RPC采集（Thrift RPC过程采集）
     */
    public static final CommonSwitcher ENABLE_THRIFT_RPC_COLLECT = new CommonSwitcher(252, "节点-是否启用RPC采集", true);

    /**
     * 节点-是否启用spring容器 (是否是Spring项目)
     */
    public static final CommonSwitcher ENABLE_SPRING_CONTEXT = new CommonSwitcher(300, "节点-是否启用spring容器 (是否是Spring项目)", true);









}
