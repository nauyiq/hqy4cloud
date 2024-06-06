package com.hqy.cloud.stream.api;

import com.hqy.cloud.common.base.lang.StringConstants;

/**
 * 表示消息的id， id必须实现Comparable接口，即id应该是可以比较、排序的
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/24
 */
public interface MessageId<T extends Comparable<T>> {

    /**
     * 表示当前id的使用场景, 指定哪个业务？哪个场景下使用的
     * @return 使用场景, 默认为 "default"
     */
    default String scene() {
        return StringConstants.DEFAULT;
    }

    /**
     * 获取ID值
     * @return 获取当前ID值, 可以为NULL。
     */
    T get();


}
