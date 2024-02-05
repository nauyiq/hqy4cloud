package com.hqy.cloud.shardingsphere.algorithm;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/5
 */
@FunctionalInterface
public interface NodeNameMatchingFunction {

    /**
     * 数据库名、表名是否符合要求.
     * @param name 数据库名、表名
     * @return     是否符合要求
     */
    boolean isMatching(String name);

}
