package com.hqy.cloud.common.swticher;

import java.io.Serial;

/**
 * 通用的节点开关
 * @author qy
 * @date 2021-07-28 9:35
 */
public class CommonSwitcher extends AbstractSwitcher {

    @Serial
    private static final long serialVersionUID = -5097824901579636026L;

    private boolean registerActuator;

    protected CommonSwitcher(int id, String name, boolean status) {
        this(id, name, status, true);
    }

    protected CommonSwitcher(int id, String name, boolean status, boolean registerActuator) {
        super(id, name, status);
        this.registerActuator = registerActuator;
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
     * 节点-socket.io项目是否接入网关Gateway （默认打开）
     */
    public static final CommonSwitcher ENABLE_GATEWAY_SOCKET_AUTHORIZE = new CommonSwitcher(202, "节点-socket.io项目是否接入网关Gateway", true);

    /**
     * 节点-是否采用基于Lettuce的RedisTemplate （默认true）
     */
    public static final CommonSwitcher ENABLE_LETTUCE_REDIS_TEMPLATE = new CommonSwitcher(203, "节点-是否采用基于Lettuce的RedisTemplate", true);

    /**
     * 节点-是否启用RPC接口采集
     */
    public static final CommonSwitcher ENABLE_THRIFT_RPC_COLLECTION = new CommonSwitcher(204, "节点-是否启用RPC接口采集", true);

    /**
     * 节点-是否采用Thrift-rpc调用链持久化
     */
    public static final CommonSwitcher ENABLE_THRIFT_RPC_CALL_CHAIN_PERSISTENCE = new CommonSwitcher(205, "节点-是否采用Thrift-rpc调用链持久化", true);

    /**
     * 节点-是否采用分布式事务传播
     */
    public static final CommonSwitcher ENABLE_PROPAGATE_GLOBAL_TRANSACTION = new CommonSwitcher(206, "节点-是否采用分布式事务传播", true);

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
    public static final CommonSwitcher ENABLE_THRIFT_SERVER_BEAN = new CommonSwitcher(209, "节点-使用ThriftServer-Bean, 标志为RPC的提供者", true);

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
     * 节点-是否采用自定义的网关负载均衡策略
     */
    public static final CommonSwitcher ENABLE_CUSTOMER_GATEWAY_LOAD_BALANCE = new CommonSwitcher(214, "节点-是否采用自定义的网关负载均衡策略", true);

    /**
     * 节点-是否启用RPC CLIENT CHANNEL内存泄露保护开关
     */
    public static final CommonSwitcher ENABLE_RPC_CLIENT_CHANNEL_LEAK_PROTECTION = new CommonSwitcher(215, "节点-是否启用RPC CLIENT CHANNEL内存泄露保护开关",true);

    /**
     * 节点-节点-是否采用集群限流统计计数方式
     */
    public static final CommonSwitcher ENABLE_CLUSTER_LIMITING_REQUEST_STATISTICS = new CommonSwitcher(216, "节点-是否采用集群限流统计计数方式", false);

    /**
     * 节点-是否启用FailBack Registry重试检查，判断是否要跳过failBack
     */
    public static final CommonSwitcher ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK = new CommonSwitcher(217, "节点-是否启用FailBack Registry重试检查，判断是否要跳过failBack", true);

    /**
     * 节点-是否采用基于本地pid进行计算的值作为雪花算法的workerId 默认true
     */
    public static final CommonSwitcher ENABLE_USING_PID_SNOWFLAKE_WORKER_ID = new CommonSwitcher(218, "节点-是否采用基于本地pid进行计算的值作为雪花算法的workerId", true);

    /**
     * 节点 - 是否兼容Thrift server自定义通用异常
     */
    public static final CommonSwitcher ENABLE_THRIFT_RPC_COMMON_EXCEPTION = new CommonSwitcher(219, "节点-是否兼容Thrift server自定义通用异常", true);

    /**
     * 节点-是否基于环境进行canal的隔离
     */
    public static final CommonSwitcher ENABLE_CANAL_ENV_ISO = new CommonSwitcher(220, "节点-是否基于环境进行canal的隔离", false);

    /**
     * 节点-是否开启actuator端口Http Basic认证 (默认打开)
     */
    public static final CommonSwitcher ENABLE_ACTUATOR_BASIC_AUTHORIZATION = new CommonSwitcher(221, "节点-是否开启actuator端口Http Basic认证", true);

    /**
     * 节点-是否允许使用账号RPC查询校验Actuator-Http-Basic
     * 默认关闭，关闭情况下 只采用配置中心 + 内存的方式校验http basic认证
     */
    public static final CommonSwitcher ENABLE_ACCOUNT_RPC_QUERY_ACTUATOR_BASIC_AUTHORIZATION = new CommonSwitcher(222, "节点-是否允许使用账号RPC查询校验Actuator-Http-Basic", false);

    /**
     * 节点-是否开启数据库慢sql采集
     * 只针对配置了数据库的服务生效
     */
    public static final CommonSwitcher ENABLE_DATABASE_SLOW_SQL_COLLECTION = new CommonSwitcher(223, "节点-是否开启数据库慢sql采集", true);

    /**
     * 节点-是否开启数据库异常sql采集
     * 只针对配置了数据库的服务生效
     */
    public static final CommonSwitcher ENABLE_DATABASE_ERROR_SQL_COLLECTION = new CommonSwitcher(224, "节点-是否开启数据库异常sql采集", true);

    /**
     * 节点-是否开启异常采集器 默认打开
     */
    public static final CommonSwitcher ENABLE_EXCEPTION_COLLECTOR = new CommonSwitcher(225, "节点-是否开启异常采集器", true);


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
    public static final CommonSwitcher JUST_4_TEST_DEBUG = new CommonSwitcher(250,"节点-DEBUG开关（默认关）",true);

    /**
     * 节点-是否采用上下文里的白名单
     */
    public static final CommonSwitcher ENABLE_PROJECT_CONTEXT_WHITE = new CommonSwitcher(251, "节点-是否采用上下文里的白名单", true);

    /**
     * 节点-是否启用RPC采集（Thrift RPC过程采集）
     */
    public static final CommonSwitcher ENABLE_THRIFT_RPC_COLLECT = new CommonSwitcher(252, "节点-是否启用RPC采集", true);

    /**
     * 节点-是否禁用SpringBoot热部署 (默认 true)
     */
    public static final CommonSwitcher ENABLE_SPRING_BOOT_RESTART_DEVTOOLS = new CommonSwitcher(253, "节点-是否禁用Springboot热部署", true);


    /**
     * 节点-Thrift rpc 发生异常时是否关闭channel （默认 false）
     */
    public static final CommonSwitcher ENABLE_CLOSE_THRIFT_CHANNEL_ON_ERROR = new CommonSwitcher(254, "节点-Thrift Rpc发生异常时是否关闭通道", false);


    public boolean isRegisterActuator() {
        return registerActuator;
    }

    public void setRegisterActuator(boolean registerActuator) {
        this.registerActuator = registerActuator;
    }
}
