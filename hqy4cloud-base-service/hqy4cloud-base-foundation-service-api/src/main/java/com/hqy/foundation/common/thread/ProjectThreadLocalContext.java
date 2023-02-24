package com.hqy.foundation.common.thread;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/22 14:26
 */
public class ProjectThreadLocalContext {

    public static final ThreadLocal<Long> PARENT_ID = new ThreadLocal<>();

    public static final ThreadLocal<Long> ROOT_ID = new ThreadLocal<>();


}
