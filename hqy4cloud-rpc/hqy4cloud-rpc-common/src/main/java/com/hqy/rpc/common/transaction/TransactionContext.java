package com.hqy.rpc.common.transaction;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 事务执行上下文, 标记当前rpc方法是否需要进行事务传播
 *
 * rpc调用过程Seata分布式事务传播的问题
 * 解决方案：
 * 1.远程服务调用方：
 *      发起远程服务调用时，需要把全局事务XID包含到请求信息中
 * 2.远程服务提供方：
 *      处理请求前，解析获取 XID 并绑定到 RootContext 中
 *      处理请求后，将 XID 从 RootContext 中解绑
 *
 * https://blog.csdn.net/fu_huo_1993/article/details/120267484
 * Seata事务管理的原理：
 * Seata 的事务上下文由 RootContext 来管理。
 * 应用开启一个全局事务后，RootContext 会自动绑定该事务的 XID，事务结束（提交或回滚完成），RootContext 会自动解绑 XID。<br/>
 * 应用可以通过 RootContext 的 API 接口（RootContext.getXID()）来获取当前运行时的全局事务 XID <br/>
 * 应用是否运行在一个全局事务的上下文中，就是通过 RootContext 是否绑定 XID 来判定的 <br/>
 * seata分布式事务在微服务框架的实现中 主要依赖于全局事务id的传播，并且需要把这个全局事务ID在各个微服务间传播。
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/28 14:30
 */
public class TransactionContext {

    private static final Logger log = LoggerFactory.getLogger(TransactionContext.class);

    private TransactionContext() {}

    /**
     * 需要传播事务的rpc方法集合
     */
    private static final Set<String> TRANSACTION_METHOD = new CopyOnWriteArraySet<>();



    /**
     * 标记某个rpc方法需要开启分布式事务.
     * @param methodName rpc方法名
     */
    public static void addTransaction(String... methodName) {
        if (Objects.isNull(methodName) || StringUtils.isEmpty(methodName)) {
            return;
        }
        TRANSACTION_METHOD.addAll(Arrays.asList(methodName));
    }

    /**
     * 判断当前rpc方法是否需要开启分布式事务. 如果是则需要进行xid的传播.
     * @param methodName rpc方法
     * @return true or false
     */
    public static boolean isTransactional(String methodName) {
        if (methodName.startsWith(MicroServiceConstants.COMMON_COLLECTOR)) {
            return false;
        }

        return TRANSACTION_METHOD.stream().anyMatch(methodName::contains);
    }

    /**
     * 确认标记当前rpc方法是否需要进行事务传播
     * @param method thrift rpc remote method.
     */
    public static void makeThriftMethodTransactional(Method method) {
        String methodName = method.getName();
        if (CommonSwitcher.ENABLE_PROPAGATE_GLOBAL_TRANSACTION.isOff()) {
            log.info("Not register [{}] is globalTransactional method.", methodName);
        } else {
            if (TransactionContext.isTransactional(methodName)) {
                return;
            }
            GlobalTransactionalThriftMethod annotation = method.getAnnotation(GlobalTransactionalThriftMethod.class);
            if (annotation != null) {
                TransactionContext.addTransaction(methodName);
            }
        }
    }
}
