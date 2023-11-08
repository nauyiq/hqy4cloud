package com.hqy.cloud.canal.core.adapter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:28
 */
public interface SourceAdapter<SOURCE, SINK> {
    SINK adapt(SOURCE source);
}
