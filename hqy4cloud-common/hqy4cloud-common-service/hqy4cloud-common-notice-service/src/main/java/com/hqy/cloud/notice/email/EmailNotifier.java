package com.hqy.cloud.notice.email;

import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.cloud.service.EmailRemoteService;
import com.hqy.foundation.notice.AbstractNotifier;
import com.hqy.foundation.notice.NoticeTarget;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 16:34
 */
public class EmailNotifier extends AbstractNotifier {

    public EmailNotifier(Environment environment) {
        super(environment);
    }

    @Override
    protected <T> void doNotify(List<NoticeTarget> targets, T content) {
        if (content instanceof EmailContent emailContent) {
            EmailRemoteService remoteService = RPCClient.getRemoteService(EmailRemoteService.class);
            remoteService.senderHtmlEmails(emailContent.getSender(),
                    targets.stream().map(NoticeTarget::getTarget).collect(Collectors.toSet()), emailContent.getSubject(), emailContent.getContent());
        }
    }
}
