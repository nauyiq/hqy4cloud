package com.hqy.rpc.common.transaction;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记一个thrift rpc方法为某个分布式事务中的链条, 使得当前rpc方法调用过程中进行分布式事务的传播
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/28 13:57
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD})
public @interface GlobalTransactionalThriftMethod {
}
