package com.hqy.rpc.thrift;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

/**
 * 在rpc调用过程中 动态拼接参数类 用于拓展 识别当前调用链. 标志oneway等 <br>
 * 在thrift.rpc、nifty中 oneway并不能区分同步调用还是异步调用 oneway是nifty的一个特性<br>
 * 在apache.thrift中 oneway表示客户端是否关心返回结果。 oneway=true 表示不关心返回结果， 此时可以稍微提升接口性能. 当不可以表示当前就是异步调用.
 *
 * 因此在此框架中。 修改nifty源码。 在rpc调用中oneway = true 的rpc方法改为异步调用.
 *
 * 每个transaction中 有参数:    RootId，用于标识唯一的一个调用链;
 *                           ParentId，父Id是谁？谁在调用我；
 *                           ChildId，我在调用谁?
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/21 18:01
 */
@ThriftStruct
public final class RemoteExParam {

    /**
     * root id 用于标识唯一的一个调用链
     */
    @ThriftField(1)
    public String rootId;

    /**
     * 父id 在一个transaction中 表示当前调用链中谁在调用我
     */
    @ThriftField(2)
    public String parentId;

    /**
     * 子id 在一个transaction中 表示当前调用链中我在调用谁
     */
    @ThriftField(3)
    public String childId;

    /**
     * 表示当前rpc方法是否是oneway
     */
    @ThriftField(4)
    public boolean oneway;

    /**
     * 在seata分布式事务中 的xid（全局事务的唯一id）；
     */
    @ThriftField(5)
    public String xid;



    public RemoteExParam() {
    }


    public RemoteExParam(String rootId, String parentId, String childId, boolean oneway) {
        this.rootId = rootId;
        this.parentId = parentId;
        this.childId = childId;
        this.oneway = oneway;
    }
}
