package com.hqy.rpc.cluster.router;

import com.hqy.rpc.common.Metadata;

/**
 * abstract Router.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 17:28
 */
public abstract class AbstractRouter implements Router {

    protected static final transient String FORCE_KEY = "force";
    protected Metadata metadata;
    protected int priority;

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Router o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
