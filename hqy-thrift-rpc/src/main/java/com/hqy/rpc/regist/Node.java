package com.hqy.rpc.regist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 10:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Node {

    /**
     * 默认的hash因子
     */
    public static final String DEFAULT_HASH_FACTOR = "default";

    /**
     * 当前节点在注册中心是否是脱机状态... true表示存活
     */
    protected Boolean alive = true;

    /**
     * 当前服务所属环境
     */
    protected String env;

    /**
     * 节点名称 (中文名)
     */
    protected String name;

    /**
     * 节点名称 (英文名)
     */
    protected String nameEn;

    /**
     * 哈希因子，区分集群中的某个节点时使用
     */
    protected String hashFactor = DEFAULT_HASH_FACTOR;

    /**
     * 使用的ip，端口等信息
     */
    protected UsingIpPort uip;

}
