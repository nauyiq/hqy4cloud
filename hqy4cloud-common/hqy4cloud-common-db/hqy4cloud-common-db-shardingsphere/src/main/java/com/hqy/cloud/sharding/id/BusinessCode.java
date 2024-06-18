package com.hqy.cloud.sharding.id;

/**
 * 业务code， 用于分表和生成id策略
 * @author qiyuan.hong
 * @date 2024/7/19
 */
public interface BusinessCode {

    /**
     * 获取业务代码
     * @return
     */
    String getBusinessCode();

    /**
     * 获取分表的数据
     * @return
     */
    int getTableCount();

}
