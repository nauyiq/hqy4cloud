package com.hqy.cloud.alarm.notification.api;

import com.hqy.cloud.alarm.notification.common.NoticeTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

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

    @Override
    public <T extends NotificationContent> void notify(T content, NotificationController config) {
        // 获取通知对象
        List<NoticeTarget> targets = config.target();
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
    protected abstract <T extends NotificationContent> void doNotify(List<NoticeTarget> targets, T content);
}
