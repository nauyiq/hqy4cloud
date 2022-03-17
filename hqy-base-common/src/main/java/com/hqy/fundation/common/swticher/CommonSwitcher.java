package com.hqy.fundation.common.swticher;

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
     * 节点-测试开关（仅仅用于开关测试，不要用于业务规则判定）
     */
    public static final CommonSwitcher JUST_4_TEST_DEBUG = new CommonSwitcher(250,"节点-DEBUG开关（默认关）",false);

    /**
     * 节点-是否采用上下文里的白名单
     */
    public static final CommonSwitcher ENABLE_PROJECT_CONTEXT_WHITE = new CommonSwitcher(251, "节点-是否采用上下文里的白名单", true);

    /**
     * 节点-是否启用spring容器 (是否是Spring项目)
     */
    public static final CommonSwitcher ENABLE_SPRING_CONTEXT = new CommonSwitcher(300, "节点-是否启用spring容器 (是否是Spring项目)", true);









}
