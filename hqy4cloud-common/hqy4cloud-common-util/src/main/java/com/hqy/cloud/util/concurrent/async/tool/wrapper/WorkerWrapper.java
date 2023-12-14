package com.hqy.cloud.util.concurrent.async.tool.wrapper;

import cn.hutool.core.date.SystemClock;
import com.hqy.cloud.util.concurrent.async.tool.callback.ICallback;
import com.hqy.cloud.util.concurrent.async.tool.exeception.SkippedException;
import com.hqy.cloud.util.concurrent.async.tool.worker.IWorker;
import com.hqy.cloud.util.concurrent.async.tool.worker.ResultState;
import com.hqy.cloud.util.concurrent.async.tool.worker.WorkerResult;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对每个worker及callback进行包装，一对一
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 13:47
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class WorkerWrapper<T, V> {

    /**
     * 该wrapper的唯一标识
     */
    private final String id;

    /**
     * 封装的Worker
     */
    private final IWorker<T, V> worker;

    /**
     * 封装的callback
     */
    private final ICallback<T, V> callback;

    /**
     * worker将来要处理的param
     */
    private T param;

    /**
     * 在自己后面的wrapper，如果没有，自己就是末尾；如果有一个，就是串行；如果有多个，有几个就需要开几个线程</p>
     * -------2
     * 1
     * -------3
     * 如1后面有2、3
     */
    private List<WorkerWrapper<?, ?>> nextWrappers;

    /**
     * 依赖的wrappers，有2种情况，1:必须依赖的全部完成后，才能执行自己 2:依赖的任何一个、多个完成了，就可以执行自己
     * 通过must字段来控制是否依赖项必须完成
     * 1
     * -------3
     * 2
     * 1、2执行完毕后才能执行3
     */
    private List<DependWrapper> dependWrappers;

    /**
     * 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
     * 经试验,volatile并不能保证"同一毫秒"内,多线程对该值的修改和拉取
     * <p>
     * 1-finish, 2-error, 3-working
     */
    private final AtomicInteger state = new AtomicInteger(0);


    /**
     * 该map存放所有wrapper的id和wrapper映射
     */
    private Map<String, WorkerWrapper> forParamUseWrappers;

    /**
     * 也是个钩子变量，用来存临时的结果
     */
    private volatile WorkerResult<V> workerResult = WorkerResult.ofDefault();

    /**
     * 是否在执行自己前，去校验nextWrapper的执行结果<p>
     * 1   4
     * -------3
     * 2
     * 如这种在4执行前，可能3已经执行完毕了（被2执行完后触发的），那么4就没必要执行了。
     * 注意，该属性仅在nextWrapper数量<=1时有效，>1时的情况是不存在的
     */
    private volatile boolean needCheckNextWrapperResult = true;

    private static final int FINISH = 1;
    private static final int ERROR = 2;
    private static final int WORKING = 3;
    private static final int INIT = 0;

    private WorkerWrapper(String id, IWorker<T, V> worker, T param, ICallback<T, V> callback) {
        if (worker == null) {
            throw new NullPointerException("async.worker is null");
        }
        this.worker = worker;
        this.param = param;
        this.id = id;
        //允许不设置回调
        if (callback == null) {
            callback = new ICallback<>() {};
        }
        this.callback = callback;
    }


    /**
     * 开始工作
     * @param executorService     工作线程池
     * @param timeout             超时时间
     * @param forParamUseWrappers 需要执行的worker包装
     */
    public void work(ExecutorService executorService, long timeout, Map<String, WorkerWrapper> forParamUseWrappers) {
        work(executorService, null, timeout, forParamUseWrappers);
    }

    /**
     * 总控制台超时，停止所有任务
     */
    public void stopNow() {
        if (getState() == INIT || getState() == WORKING) {
            fastFail(getState(), null);
        }
    }


    /**
     * 开始工作
     * @param executorService     工作线程池
     * @param fromWrapper         代表这次work是由哪个上游wrapper发起的
     * @param remainTime          整体任务的剩余时间
     * @param forParamUseWrappers 用到的所有wrapper
     */
    private void work(ExecutorService executorService, WorkerWrapper fromWrapper, long remainTime, Map<String, WorkerWrapper> forParamUseWrappers) {
        this.forParamUseWrappers = forParamUseWrappers;
        //将自己放到所有wrapper的集合里去
        forParamUseWrappers.put(id, this);
        long now = SystemClock.now();
        //总的已经超时了，就快速失败，进行下一个
        if (remainTime <= 0) {
            fastFail(INIT, null);
            beginNext(executorService, now, remainTime);
            return;
        }

        //如果自己已经执行过了。
        //可能有多个依赖，其中的一个依赖已经执行完了，并且自己也已开始执行或执行完毕。当另一个依赖执行完毕，又进来该方法时，就不重复处理了
        if (getState() == FINISH || getState() == ERROR) {
            beginNext(executorService, now, remainTime);
            return;
        }

        //如果在执行前需要校验nextWrapper的状态
        if (needCheckNextWrapperResult) {
            //如果自己的next链上有已经出结果或已经开始执行的任务了，自己就不用继续了
            if (!checkNextWrapperResult()) {
                fastFail(INIT, new SkippedException());
                beginNext(executorService, now, remainTime);
                return;
            }
        }

        //如果没有任何依赖，说明自己就是第一批要执行的
        if (dependWrappers == null || dependWrappers.size() == 0) {
            fire();
            beginNext(executorService, now, remainTime);
            return;
        }

        /*如果有前方依赖，存在两种情况
         一种是前面只有一个wrapper。即 A  ->  B
        一种是前面有多个wrapper。A C D ->   B。需要A、C、D都完成了才能轮到B。但是无论是A执行完，还是C执行完，都会去唤醒B。
        所以需要B来做判断，必须A、C、D都完成，自己才能执行 */

        //只有一个依赖
        if (dependWrappers.size() == 1) {
            doDependsOneJob(fromWrapper);
            beginNext(executorService, now, remainTime);
        } else {
            //有多个依赖时
            doDependsJobs(executorService, dependWrappers, fromWrapper, now, remainTime);
        }

    }

    private void doDependsOneJob(WorkerWrapper dependWrapper) {
        if (ResultState.TIMEOUT == dependWrapper.getWorkerResult().getState()) {
            workerResult = defaultResult();
            fastFail(INIT, null);
        } else if (ResultState.EXCEPTION == dependWrapper.getWorkerResult().getState()) {
            workerResult = defaultExResult(dependWrapper.getWorkerResult().getCause());
            fastFail(INIT, null);
        } else {
            //前面任务正常完毕了，该自己了
            fire();
        }
    }

    private synchronized void doDependsJobs(ExecutorService executorService, List<DependWrapper> dependWrappers, WorkerWrapper fromWrapper, long now, long remainTime) {
        boolean nowDependIsMust = false;
        //创建必须完成的上游wrapper集合
        Set<DependWrapper> mustWrapper = new HashSet<>();
        for (DependWrapper dependWrapper : dependWrappers) {
            if (dependWrapper.isMust()) {
                mustWrapper.add(dependWrapper);
            }
            if (dependWrapper.getDependWrapper().equals(fromWrapper)) {
                nowDependIsMust = dependWrapper.isMust();
            }
        }

        //如果全部是不必须的条件，那么只要到了这里，就执行自己。
        if (mustWrapper.size() == 0) {
            if (ResultState.TIMEOUT == fromWrapper.getWorkerResult().getState()) {
                fastFail(INIT, null);
            } else {
                fire();
            }
            beginNext(executorService, now, remainTime);
            return;
        }

        //如果存在需要必须完成的，且fromWrapper不是必须的，就什么也不干
        if (!nowDependIsMust) {
            return;
        }

        //如果fromWrapper是必须的
        boolean existNoFinish = false;
        boolean hasError = false;
        //先判断前面必须要执行的依赖任务的执行结果，如果有任何一个失败，那就不用走action了，直接给自己设置为失败，进行下一步就是了
        for (DependWrapper dependWrapper : mustWrapper) {
            WorkerWrapper workerWrapper = dependWrapper.getDependWrapper();
            WorkerResult tempWorkResult = workerWrapper.getWorkerResult();
            //为null或者isWorking，说明它依赖的某个任务还没执行到或没执行完
            if (workerWrapper.getState() == INIT || workerWrapper.getState() == WORKING) {
                existNoFinish = true;
                break;
            }
            if (ResultState.TIMEOUT == tempWorkResult.getState()) {
                workerResult = defaultResult();
                hasError = true;
                break;
            }
            if (ResultState.EXCEPTION == tempWorkResult.getState()) {
                workerResult = defaultExResult(workerWrapper.getWorkerResult().getCause());
                hasError = true;
                break;
            }

        }
        //只要有失败的
        if (hasError) {
            fastFail(INIT, null);
            beginNext(executorService, now, remainTime);
            return;
        }

        //如果上游都没有失败，分为两种情况，一种是都finish了，一种是有的在working
        //都finish的话
        if (!existNoFinish) {
            //上游都finish了，进行自己
            fire();
            beginNext(executorService, now, remainTime);
        }
    }



    /**
     * 执行自己的job.具体的执行是在另一个线程里,但判断阻塞超时是在work线程
     */
    private void fire() {
        //阻塞取结果
        workerResult = workerDoJob();
    }

    /**
     * 具体的单个worker执行任务
     */
    private WorkerResult<V> workerDoJob() {
        //避免重复执行
        if (!checkIsNullResult()) {
            return workerResult;
        }
        try {
            //如果已经不是init状态了，说明正在被执行或已执行完毕。这一步很重要，可以保证任务不被重复执行
            if (!compareAndSetState(INIT, WORKING)) {
                return workerResult;
            }
            callback.begin();
            //执行耗时操作
            V resultValue = worker.action(param, forParamUseWrappers);

            //如果状态不是在working,说明别的地方已经修改了
            if (!compareAndSetState(WORKING, FINISH)) {
                return workerResult;
            }
            workerResult.setState(ResultState.SUCCESS);
            workerResult.setResult(resultValue);
            //回调成功
            callback.result(true, param, workerResult);
            return workerResult;
        } catch (Exception e) {
            //避免重复回调
            if (!checkIsNullResult()) {
                return workerResult;
            }
            fastFail(WORKING, e);
            return workerResult;
        }


    }

    /**
     * 判断自己下游链路上，是否存在已经出结果的或已经开始执行的
     * 如果没有返回true，如果有返回false
     */
    private boolean checkNextWrapperResult() {
        //如果自己就是最后一个，或者后面有并行的多个，就返回true
        if (nextWrappers == null || nextWrappers.size() != 1) {
            return getState() == INIT;
        }
        WorkerWrapper nextWrapper = nextWrappers.get(0);
        boolean state = nextWrapper.getState() == INIT;
        return state && nextWrapper.checkNextWrapperResult();
    }


    private void beginNext(ExecutorService executorService, long now, long remainTime) {
        //花费的时间
        long costTime = SystemClock.now() - now;
        if (nextWrappers == null) {
            return;
        }
        if (nextWrappers.size() == 1) {
            nextWrappers.get(0).work(executorService, WorkerWrapper.this, remainTime - costTime, forParamUseWrappers);
            return;
        }
        CompletableFuture[] futures = new CompletableFuture[nextWrappers.size()];
        for (int i = 0; i < nextWrappers.size(); i++) {
            int finalI = i;
            futures[i] = CompletableFuture.runAsync(() -> nextWrappers.get(finalI)
                    .work(executorService, WorkerWrapper.this, remainTime - costTime, forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(remainTime - costTime, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean fastFail(int expect, Exception e) {
        //试图将它从expect状态,改成Error
        if (!compareAndSetState(expect, ERROR)) {
            return false;
        }
        //尚未处理过结果
        if (checkIsNullResult()) {
            if (e == null) {
                workerResult = defaultResult();
            } else {
                workerResult = defaultExResult(e);
            }
        }
        // 设置回调
        callback.result(false, param, workerResult);
        return true;
    }


    private int getState() {
        return state.get();
    }

    private boolean compareAndSetState(int expect, int update) {
        return this.state.compareAndSet(expect, update);
    }

    private boolean checkIsNullResult() {
        return ResultState.DEFAULT == workerResult.getState();
    }

    private WorkerResult<V> defaultResult() {
        workerResult.setState(ResultState.TIMEOUT);
        workerResult.setResult(worker.defaultValue());
        return workerResult;
    }

    private WorkerResult<V> defaultExResult(Exception e) {
        workerResult.setState(ResultState.EXCEPTION);
        workerResult.setResult(worker.defaultValue());
        workerResult.setCause(e);
        return workerResult;
    }

    public WorkerResult<V> getWorkerResult() {
        return workerResult;
    }

    public List<WorkerWrapper<?, ?>> getNextWrappers() {
        return nextWrappers;
    }

    public void setParam(T param) {
        this.param = param;
    }

    private void addDepend(WorkerWrapper<?, ?> workerWrapper, boolean must) {
        addDepend(new DependWrapper(workerWrapper, must));
    }

    private void addDepend(DependWrapper dependWrapper) {
        if (dependWrappers == null) {
            dependWrappers = new ArrayList<>();
        }
        //如果依赖的是重复的同一个，就不重复添加了
        for (DependWrapper wrapper : dependWrappers) {
            if (wrapper.equals(dependWrapper)) {
                return;
            }
        }
        dependWrappers.add(dependWrapper);
    }

    private void addNext(WorkerWrapper<?, ?> workerWrapper) {
        if (nextWrappers == null) {
            nextWrappers = new ArrayList<>();
        }
        //避免添加重复
        for (WorkerWrapper wrapper : nextWrappers) {
            if (workerWrapper.equals(wrapper)) {
                return;
            }
        }
        nextWrappers.add(workerWrapper);
    }

    private void addNextWrappers(List<WorkerWrapper<?, ?>> wrappers) {
        if (wrappers == null) {
            return;
        }
        for (WorkerWrapper<?, ?> wrapper : wrappers) {
            addNext(wrapper);
        }
    }

    private void addDependWrappers(List<DependWrapper> dependWrappers) {
        if (dependWrappers == null) {
            return;
        }
        for (DependWrapper wrapper : dependWrappers) {
            addDepend(wrapper);
        }
    }

    public List<DependWrapper> getDependWrappers() {
        return dependWrappers;
    }

    private void setNeedCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
        this.needCheckNextWrapperResult = needCheckNextWrapperResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkerWrapper<?, ?> that = (WorkerWrapper<?, ?>) o;
        return needCheckNextWrapperResult == that.needCheckNextWrapperResult &&
                Objects.equals(param, that.param) &&
                Objects.equals(worker, that.worker) &&
                Objects.equals(callback, that.callback) &&
                Objects.equals(nextWrappers, that.nextWrappers) &&
                Objects.equals(dependWrappers, that.dependWrappers) &&
                Objects.equals(state, that.state) &&
                Objects.equals(workerResult, that.workerResult);
    }


    @Override
    public int hashCode() {
        return Objects.hash(param, worker, callback, nextWrappers, dependWrappers, state, workerResult, needCheckNextWrapperResult);
    }


    public static class Builder<W, C> {

        /**
         * 该wrapper的唯一标识
         */
        private String id = UUID.randomUUID().toString();

        /**
         * worker将来要处理的param
         */
        private W param;
        private IWorker<W, C> worker;
        private ICallback<W, C> callback;

        /**
         * 自己后面的所有
         */
        private List<WorkerWrapper<?, ?>> nextWrappers;

        /**
         * 自己依赖的所有
         */
        private List<DependWrapper> dependWrappers;

        /**
         * 存储强依赖于自己的wrapper集合
         */
        private Set<WorkerWrapper<?, ?>> selfIsMustSet;

        private boolean needCheckNextWrapperResult = true;

        public Builder<W, C> worker(IWorker<W, C> worker) {
            this.worker = worker;
            return this;
        }

        public Builder<W, C> param(W w) {
            this.param = w;
            return this;
        }

        public Builder<W, C> id(String id) {
            if (id != null) {
                this.id = id;
            }
            return this;
        }

        public Builder<W, C> needCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
            this.needCheckNextWrapperResult = needCheckNextWrapperResult;
            return this;
        }

        public Builder<W, C> callback(ICallback<W, C> callback) {
            this.callback = callback;
            return this;
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?>... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                depend(wrapper);
            }
            return this;
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper) {
            return depend(wrapper, true);
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper, boolean isMust) {
            if (wrapper == null) {
                return this;
            }
            DependWrapper dependWrapper = new DependWrapper(wrapper, isMust);
            if (dependWrappers == null) {
                dependWrappers = new ArrayList<>();
            }
            dependWrappers.add(dependWrapper);
            return this;
        }

        public Builder<W, C> next(WorkerWrapper<?, ?> wrapper) {
            return next(wrapper, true);
        }

        public Builder<W, C> next(WorkerWrapper<?, ?> wrapper, boolean selfIsMust) {
            if (nextWrappers == null) {
                nextWrappers = new ArrayList<>();
            }
            nextWrappers.add(wrapper);

            //强依赖自己
            if (selfIsMust) {
                if (selfIsMustSet == null) {
                    selfIsMustSet = new HashSet<>();
                }
                selfIsMustSet.add(wrapper);
            }
            return this;
        }

        public Builder<W, C> next(WorkerWrapper<?, ?>... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                next(wrapper);
            }
            return this;
        }

        public WorkerWrapper<W, C> build() {
            WorkerWrapper<W, C> wrapper = new WorkerWrapper<>(id, worker, param, callback);
            wrapper.setNeedCheckNextWrapperResult(needCheckNextWrapperResult);
            if (dependWrappers != null) {
                for (DependWrapper workerWrapper : dependWrappers) {
                    workerWrapper.getDependWrapper().addNext(wrapper);
                    wrapper.addDepend(workerWrapper);
                }
            }
            if (nextWrappers != null) {
                for (WorkerWrapper<?, ?> workerWrapper : nextWrappers) {
                    boolean must = selfIsMustSet != null && selfIsMustSet.contains(workerWrapper);
                    workerWrapper.addDepend(wrapper, must);
                    wrapper.addNext(workerWrapper);
                }
            }

            return wrapper;
        }

    }
}
