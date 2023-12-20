package com.hqy.foundation.event.notice;

import com.hqy.foundation.common.EventContent;
import com.hqy.foundation.common.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 抽象的通知类, 由子类去决定通知的方式
 * 例如采用RPC或者MQ进行通知， 如果是不太紧急或者不重要的通知 可以采用RPC - ONEWAY的形式发送 通知发了就不管了 </p>
 *                         相应的如果是比较重要或者需要进行持久化的通知，可以用MQ 或者 最大努力通知的形式。。。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 16:11
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractNotifier implements Notifier {
    private final Environment environment;

    @Override
    public <T extends EventContent> void notify(EventType eventType, T content, NotificationController config) {
        // 获取通知对象
        List<NoticeTarget> targets = config.target(environment, eventType);
        if (CollectionUtils.isEmpty(targets)) {
            log.warn("Can not found notice target by {}.", config.notificationType());
            return;
        }
        // 执行业务通知
        doNotify(targets, content);
    }

    /**
     * 交给子类去做业务通知的逻辑
     * @param targets 通知的目标
     * @param content 通知的内容
     */
    protected abstract <T extends EventContent> void doNotify(List<NoticeTarget> targets, T content);
}
