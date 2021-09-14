package com.hqy.rpc.nacos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qy
 * @create 2021/8/23 23:26
 */
@Slf4j
@Component
public abstract class AbstractNacosClientWriter {

    /**
     * 是否启动...
     */
    private transient boolean isRunning = false;

    private ServerNode node = null;


}
