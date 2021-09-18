package com.hqy.fundation.common.swticher;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-28 9:35
 */
public class CommonSwitcher extends AbstractSwitcher {

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
     * 节点-同IP/Env RPC调度优先(非灰度机制才有效)
     */
    public static final CommonSwitcher ENABLE_RPC_SAME_IP_HIGH_PRIORITY = new CommonSwitcher(207,"节点-同IP/Env RPC调度优先(非灰度机制才有效)",true);

    /**
     * 节点-使用ThriftServer-Bean, 标志为RPC的提供者
     */
    public static final CommonSwitcher ENABLE_THRIFT_SERVER_BEAN = new CommonSwitcher(210, "节点-使用ThriftServer-Bean, 标志为RPC的提供者", true);

    /**
     * 节点-测试开关（仅仅用于开关测试，不要用于业务规则判定）
     */
    public static final CommonSwitcher JUST_4_TEST_DEBUG = new CommonSwitcher(250,"节点-DEBUG开关（默认关）",false);

    /**
     * 节点-是否启用spring容器 (是否是Spring项目)
     */
    public static final CommonSwitcher ENABLE_SPRING_CONTEXT = new CommonSwitcher(300, "节点-是否启用spring容器 (是否是Spring项目)", true);







}
