package com.hqy.fundation.common.swticher;

/**
 * 服务端内部性能开关
 * @author qy
 * @create 2021/8/23 22:41
 */
public class InternalGeneralSwitcher extends CommonSwitcher  {


    protected InternalGeneralSwitcher(int id, String name, boolean status) {
        super(id, name, status);
    }

    /**
     * 节点-采集服务消息队列是否采用过期队列(默认启用 ttl:30s)
     */
    public static final InternalGeneralSwitcher ENABLE_COLL_TTL_MESSAGE_QUEUE = new InternalGeneralSwitcher(301,"节点-采集服务消息队列是否采用过期队列",true);

}
