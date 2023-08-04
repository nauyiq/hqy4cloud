package com.hqy.cloud.foundation.cache.support.aop;

import java.lang.annotation.*;

/**
 * 表示哪些方法需要被缓存
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 14:12
 */
@Documented
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    /**
     * 缓存名， 用于获取某个方法的缓存对象
     * @return 默认为方法名
     */
    String name() default "";

    /**
     * 获取缓存key， 用于取出缓存
     * @return 默认为方法参数拼接
     */
    String key() default "";

    /**
     * 是否同步获取
     * @return 默认为true
     */
    boolean sync() default true;



}
