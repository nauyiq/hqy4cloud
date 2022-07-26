package com.hqy.foundation.cache;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 13:46
 */
public interface Key {

    /**
     * 缓存过期时间, 单位秒
     * @return 过期时间
     */
    int expireSeconds();

    /**
     * 获取缓存的前缀
     * @return 缓存key 前缀
     */
    String key();



}
