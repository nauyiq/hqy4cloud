package com.hqy.rpc.nacos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author qy
 * @create 2021/8/23 23:26
 */
@Component
public abstract class AbstractNacosClientWriter {

    private static final Logger log = LoggerFactory.getLogger(AbstractNacosClientWriter.class);

    /**
     * 当前服务节点
     */
    private static NacosNode node = null;

    /**
     * 是否启动...
     */
    private transient boolean isRunning = false;








}
