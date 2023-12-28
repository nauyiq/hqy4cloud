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



    protected CommonSwitcher(int id, String name, boolean status) {
        this(id, name, status, true);
    }

    protected CommonSwitcher(int id, String name, boolean status, boolean registerActuator) {
        super(id, name, status, registerActuator);
    }


    /**
     * 节点-是否启用RPC采集（Thrift RPC过程采集）
     */
    public static final CommonSwitcher ENABLE_THRIFT_RPC_COLLECT = new CommonSwitcher(200, "节点-是否启用RPC采集", true);

    /**
     * 节点-是否启用灰度机制？启用后 需要区分灰度与白度，否则不区分
     */
    public static final CommonSwitcher ENABLE_GRAY_MECHANISM = new CommonSwitcher(201,"节点-是否启用灰度机制",false);

    /**
     * 节点-同IP/Env RPC调度优先(非灰度机制才有效)
     */
    public static final CommonSwitcher ENABLE_RPC_SAME_IP_HIGH_PRIORITY = new CommonSwitcher(202,"节点-同IP/Env RPC调度优先(非灰度机制才有效)",true);

    /**
     * 节点-使用ThriftServer-Bean, 标志为RPC的提供者
     */
    public static final CommonSwitcher ENABLE_THRIFT_SERVER_BEAN = new CommonSwitcher(203, "节点-使用ThriftServer-Bean, 标志为RPC的提供者", true);

    /**
     * 节点-Thrift rpc 发生异常时是否关闭channel （默认 false）
     */
    public static final CommonSwitcher ENABLE_CLOSE_THRIFT_CHANNEL_ON_ERROR = new CommonSwitcher(204, "节点-Thrift Rpc发生异常时是否关闭通道", false);

    /**
     * 节点-是否启用FailBack Registry重试检查，判断是否要跳过failBack
     */
    public static final CommonSwitcher ENABLE_FAIL_BACK_REGISTRY_RETRY_CHECK = new CommonSwitcher(205, "节点-是否启用FailBack Registry重试检查，判断是否要跳过failBack", true);

    /**
     * 节点-是否启用RPC CLIENT CHANNEL内存泄露保护开关
     */
    public static final CommonSwitcher ENABLE_RPC_CLIENT_CHANNEL_LEAK_PROTECTION = new CommonSwitcher(206, "节点-是否启用RPC CLIENT CHANNEL内存泄露保护开关",true);

    /**
     * 节点 - 是否兼容Thrift server自定义通用异常
     */
    public static final CommonSwitcher ENABLE_THRIFT_RPC_COMMON_EXCEPTION = new CommonSwitcher(207, "节点-是否兼容Thrift server自定义通用异常", true);

    /**
     * 节点-是否采用分布式事务传播
     */
    public static final CommonSwitcher ENABLE_PROPAGATE_GLOBAL_TRANSACTION = new CommonSwitcher(208, "节点-是否采用分布式事务传播", true);

    /**
     * 节点-是否采用Sentinel-Thrift适配器
     * 开启后, RPC调用会记录到sentinel中
     */
    public static final CommonSwitcher ENABLE_RPC_SENTINEL_ADAPTOR_HANDLER = new CommonSwitcher(209, "节点-是否采用Sentinel-Thrift适配器", true);

    /**
     * 节点-是否允许使用账号RPC查询校验Actuator-Http-Basic
     * 默认关闭，关闭情况下 只采用配置中心 + 内存的方式校验http basic认证
     */
    public static final CommonSwitcher ENABLE_ACCOUNT_RPC_QUERY_ACTUATOR_BASIC_AUTHORIZATION = new CommonSwitcher(210, "节点-是否允许使用账号RPC查询校验Actuator-Http-Basic", false);

    /**
     * 节点-框架socket-polling环节泄漏HandshakeData兼容，默认测试环境开启.
     * 场景一： 网络原因，没升级到websocket
     * 场景二：恶意的polling的操作 导致的 addClient的uuidMap 泄漏
     */
    public static final CommonSwitcher SOCKET_POLLING_HANDSHAKE_DATA_LEAK = new CommonSwitcher(220, "节点-框架socket-polling环节泄漏HandshakeData监听，默认开启", true);

    /**
     * 节点-是否启用--框架 检测 allClients的内存泄漏
     */
    public static final CommonSwitcher ENABLE_NAMESPACE_CLIENTS_LEAK_PROTECTION = new CommonSwitcher(221, "节点-是否启用Namespace Clients内存泄露保护开关", true);

    /**
     * 节点-socket.io项目是否接入网关Gateway （默认打开）
     */
    public static final CommonSwitcher ENABLE_GATEWAY_SOCKET_AUTHORIZE = new CommonSwitcher(222, "节点-socket.io项目是否接入网关Gateway", true);

    /**
     * 与sid有关（重联场景是生成新的sid)
     * 无用http长连接释放（短时间泄漏）
     * 场景：业务： 兼容忽略js前端框架层面的socketIO层面 多发一次无意义请求的返回开关   <br>
     * 场景：技术：因js和netty均是异步线程，状态不一致性的情况。即潜在逻辑：建立完101、以及收到 NOOP(6)之后，多发的一次Polling长连接<br>
     */
    public static final CommonSwitcher SOCKET_POLLING_HTTP_LEAK = new CommonSwitcher(223, "节点-框架 前端多发无用http长连接释放，默认[关]", false);

    /**
     * 节点-是否启用共享的BIBlockIP清单（Redis）;如果false 各个项目自行维护本地缓存的BI的ip禁止名单。
     */
    public static final CommonSwitcher ENABLE_SHARED_BLOCK_IP_LIST = new CommonSwitcher(224,"节点-是否启用共享的BIBlockIP清单（Redis）",true);

    /**
     * 节点-是否采用基于本地pid进行计算的值作为雪花算法的workerId 默认true
     */
    public static final CommonSwitcher ENABLE_USING_PID_SNOWFLAKE_WORKER_ID = new CommonSwitcher(225, "节点-是否采用基于本地pid进行计算的值作为雪花算法的workerId", true);

    /**
     * 节点-是否基于环境进行canal的隔离
     */
    public static final CommonSwitcher ENABLE_CANAL_ENV_ISO = new CommonSwitcher(226, "节点-是否基于环境进行canal的隔离", false);

    /**
     * 节点-是否开启actuator端口Http Basic认证 (默认打开)
     */
    public static final CommonSwitcher ENABLE_ACTUATOR_BASIC_AUTHORIZATION = new CommonSwitcher(227, "节点-是否开启actuator端口Http Basic认证", true);

    /**
     * 节点-是否开启数据库慢sql采集
     * 只针对配置了数据库的服务生效
     */
    public static final CommonSwitcher ENABLE_DATABASE_SLOW_SQL_COLLECTION = new CommonSwitcher(228, "节点-是否开启数据库慢sql采集", true);

    /**
     * 节点-是否开启数据库异常sql采集
     * 只针对配置了数据库的服务生效
     */
    public static final CommonSwitcher ENABLE_DATABASE_ERROR_SQL_COLLECTION = new CommonSwitcher(229, "节点-是否开启数据库异常sql采集", true);

    /**
     * 节点-是否开启异常采集器 默认打开
     */
    public static final CommonSwitcher ENABLE_EXCEPTION_COLLECTOR = new CommonSwitcher(230, "节点-是否开启异常采集器", true);

    /**
     * 节点-shardingsphere是否注入druid filters
     * 此开关不注册到actuator, 因为开关在服务启动后只会生效一次。
     */
    public static final CommonSwitcher ENABLE_SHARDINGSPHERE_REGISTER_DRUID_FILTERS = new CommonSwitcher(231, "节点-shardingsphere是否注入druid filters", true, false);

    /**
     * 节点-是否定期清除druid stat 数据
     */
    public static final CommonSwitcher ENABLE_SCHEDULE_RESET_DRUID_STAT_DATA = new CommonSwitcher(232, "节点-是否定期清除druid stat 数据", true);


    /**
     * 节点-是否开启异常sql通知
     */
    public static final CommonSwitcher ENABLE_EXCEPTION_SQL_ALTER = new CommonSwitcher(233, "节点-是否启用异常sql通知", true);

    /**
     * 节点-是否启用druid basic认证传递过滤器
     */
    public static final CommonSwitcher ENABLE_TRANSLATE_DRUID_BASIC_AUTH_FILTER = new CommonSwitcher(234, "节点-是否启用druid basic认证传递过滤器", true);

    /**
     * 节点-是否开启mock ip测试
     */
    public static final CommonSwitcher ENABLE_REQUEST_MOCK_IP = new CommonSwitcher(235, "是否开启mock ip测试", false);

    /**
     * 节点-是否开启redis缓存token时采用json序列化
     */
    public static final CommonSwitcher ENABLE_REDIS_JSON_SERIAL_TOKEN_VALUE_STORE = new CommonSwitcher(236, "是否开启redis缓存token时采用json序列化", false);


    /**
     * 节点-是否禁用SpringBoot热部署 (默认 true)
     */
    public static final CommonSwitcher ENABLE_SPRING_BOOT_RESTART_DEVTOOLS = new CommonSwitcher(240, "节点-是否禁用Springboot热部署", true);


    /**
     * 节点-测试开关（仅仅用于开关测试，不要用于业务规则判定）
     */
    public static final CommonSwitcher JUST_4_TEST_DEBUG = new CommonSwitcher(250,"节点-DEBUG开关（默认关）",true);



}
