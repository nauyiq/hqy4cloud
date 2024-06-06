package com.hqy.cloud.mq.rocket.canal;

import com.hqy.cloud.canal.core.CanalGlue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * 由子类自己定义@RocketMQMessageListener, 确定要监听主题和消费者组等其他配置.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/6
 */
@Slf4j
public abstract class RocketCanalListener implements RocketMQListener<String> {
    private final CanalGlue canalGlue;

    protected RocketCanalListener(CanalGlue canalGlue) {
        this.canalGlue = canalGlue;
    }

    @Override
    public void onMessage(String message) {
        if (StringUtils.isBlank(message)) {
            log.warn("Receive empty message by canal-rocketmq message.");
            return;
        }
        canalGlue.process(message);
    }
}
