package com.hqy.cloud.notice.email;

import cn.hutool.core.date.SystemClock;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.service.EmailRemoteService;
import com.hqy.foundation.common.EventContent;
import com.hqy.foundation.event.notice.AbstractNotifier;
import com.hqy.foundation.event.notice.NoticeTarget;
import com.hqy.foundation.event.notice.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 16:34
 */
@Slf4j
public class EmailNotifier extends AbstractNotifier {
    private final EmailNotifierConfig config;
    private final Cache<String, Long> cache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(10000).build();

    public EmailNotifier(Environment environment, EmailNotifierConfig config) {
        super(environment);
        this.config = config;
    }


    @Override
    protected <T extends EventContent> void doNotify(List<NoticeTarget> targets, T content) {
        if (content instanceof EmailContent emailContent) {
            // 判断是否需要通知, 防止通知超限.
            String id = content.getId();
            Long rateAccess = cache.getIfPresent(id);
            long now = SystemClock.now();
            if (rateAccess != null && now - rateAccess <= config.getRateMillis()) {
                // already notice
                log.info("Already notice by email notifier, id: {}.", id);
                return;
            } else {
                cache.put(id, now);
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
