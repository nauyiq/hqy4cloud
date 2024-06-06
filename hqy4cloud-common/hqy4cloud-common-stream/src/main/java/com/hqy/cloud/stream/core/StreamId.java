package com.hqy.cloud.stream.core;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.stream.api.MessageId;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/6
 */
public class StreamId<T extends Comparable<T>> implements MessageId<T> {
    private final String scene;
    private final T id;

    public StreamId(T id) {
        this(StringConstants.DEFAULT, id);
    }

    public StreamId(String scene, T id) {
        this.scene = scene;
        this.id = id;
    }

    public static <T extends Comparable<T>> StreamId<T> of(T t) {
        return new StreamId<>(t);
    }

    public static <T extends Comparable<T>> StreamId<T> of(String scene, T t) {
        return new StreamId<>(scene, t);
    }

    @Override
    public T get() {
        return this.id;
    }

    @Override
    public String scene() {
        return this.scene;
    }
}
