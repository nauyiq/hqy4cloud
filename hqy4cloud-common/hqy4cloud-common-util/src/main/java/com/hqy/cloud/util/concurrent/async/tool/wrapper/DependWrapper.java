package com.hqy.cloud.util.concurrent.async.tool.wrapper;

import lombok.EqualsAndHashCode;

/**
 * 对依赖的wrapper的封装
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 13:50
 */
@EqualsAndHashCode
public class DependWrapper {

    private WorkerWrapper<?, ?> dependWrapper;

    /**
     * 是否该依赖必须完成后才能执行自己.<p>
     * 因为存在一个任务，依赖于多个任务，是让这多个任务全部完成后才执行自己，还是某几个执行完毕就可以执行自己
     * 如
     * 1
     * ---3
     * 2
     * 或
     * 1---3
     * 2---3
     * 这两种就不一样，上面的就是必须12都完毕，才能3
     * 下面的就是1完毕就可以3
     */
    private boolean must = true;

    public DependWrapper() {
    }

    public DependWrapper(WorkerWrapper<?, ?> dependWrapper, boolean must) {
        this.dependWrapper = dependWrapper;
        this.must = must;
    }

    public WorkerWrapper<?, ?> getDependWrapper() {
        return dependWrapper;
    }

    public void setDependWrapper(WorkerWrapper<?, ?> dependWrapper) {
        this.dependWrapper = dependWrapper;
    }

    public boolean isMust() {
        return must;
    }

    public void setMust(boolean must) {
        this.must = must;
    }

    @Override
    public String toString() {
        return "DependWrapper{" +
                "dependWrapper=" + dependWrapper +
                ", must=" + must +
                '}';
    }

}
