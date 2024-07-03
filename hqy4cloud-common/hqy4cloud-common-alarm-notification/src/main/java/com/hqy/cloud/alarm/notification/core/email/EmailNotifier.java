package com.hqy.cloud.alarm.notification.core.email;

import com.hqy.cloud.alarm.notification.api.AbstractNotifier;
import com.hqy.cloud.alarm.notification.common.NoticeTarget;
import com.hqy.cloud.alarm.notification.common.NotificationType;
import com.hqy.cloud.alarm.notification.config.BusinessNotificationProperties;
import com.hqy.cloud.alarm.notification.config.EmailNotifierConfigProperties;
import com.hqy.cloud.limiter.flow.AccessFlowController;
import com.hqy.cloud.limiter.flow.FlowLimitConfig;
import com.hqy.cloud.limiter.flow.FlowResult;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.service.EmailRemoteService;
import com.hqy.cloud.alarm.notification.api.NotificationContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 16:34
 */
@Slf4j
public class EmailNotifier extends AbstractNotifier {
    private final EmailNotifierConfigProperties config;
    private final AccessFlowController flowController;

    public EmailNotifier(EmailNotifierConfigProperties config) {
        this.config = config;
        // 创建限流控制器
        BusinessNotificationProperties.Limit limit = config.getLimit();
        FlowLimitConfig limitConfig = FlowLimitConfig.of(limit.getCount(), limit.getWindowSize());
        this.flowController = new AccessFlowController(limitConfig);
    }

    @Override
    protected <T extends NotificationContent> void doNotify(List<NoticeTarget> targets, T content) {
        if (content instanceof EmailContent emailContent) {
            BusinessNotificationProperties.Limit limit = config.getLimit();
            if (limit.isEnabled()) {
                // 判断是否超限
                String scene = content.scene();
                FlowResult overLimit = flowController.isOverLimit(scene);
                if (overLimit.isOverLimit()) {
                    // 超限不再发通知.
                    log.warn("Limit is over, scene:{}", scene);
                    return;
                }
            }

            // RPC发邮件.
            EmailRemoteService remoteService = RpcClient.getRemoteService(EmailRemoteService.class);
            remoteService.senderHtmlEmails(
                    StringUtils.isAllBlank(emailContent.getSender()) ? config.getSender() : emailContent.getSender(),
                    targets.stream().map(NoticeTarget::getTarget).collect(Collectors.toSet()),
                    emailContent.getSubject(),
                    emailContent.getContent());
        }
    }

    @Override
    public NotificationType type() {
        return NotificationType.EMAIL;
    }
}
