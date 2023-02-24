package com.hqy.communication.service.mail;

import com.hqy.foundation.common.bind.EmailTemplateInfo;
import com.hqy.foundation.util.AccountEmailTemplateUtil;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.util.JsonUtil;
import com.hqy.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 15:01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRemoteServiceImpl extends AbstractRPCService implements EmailRemoteService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        //创建SimpleMailMessage对象
        this.senderSimpleEmail(sender, to, subject, content);
    }

    @Override
    public void senderSimpleEmail(String sender, String to, String subject, String content) {
        if (!ValidationUtil.validateEmail(sender)) {
            log.warn("Failed execute to senSimpleEmail, {} not a email.", sender);
            return;
        }

        if (!ValidationUtil.validateEmail(to)) {
            log.warn("Failed execute to senSimpleEmail, {} not a email.", to);
            return;
        }
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }

    @Override
    public void sendSimpleEmails(Set<String> receivers, String subject, String content) {
        this.senderSimpleEmails(sender, receivers, subject, content);
    }

    @Override
    public void senderSimpleEmails(String sender, Set<String> receivers, String subject, String content) {
        if (!ValidationUtil.validateEmail(sender)) {
            log.warn("Failed execute to senSimpleEmail, {} not a email.", sender);
            return;
        }
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receivers.stream().filter(ValidationUtil::validateEmail).distinct().toArray(String[]::new));
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String content) {
        this.sendHtmlEmails(Collections.singleton(to), subject, content);
    }

    @Override
    public void senderHtmlEmail(String sender, String to, String subject, String content) {
        this.senderHtmlEmails(sender, Collections.singleton(to), subject, content);
    }

    @Override
    public void sendHtmlEmails(Set<String> receivers, String subject, String content) {
        this.senderHtmlEmails(sender, receivers, subject, content);
    }


    @Override
    public void senderHtmlEmails(String sender, Set<String> receivers, String subject, String content) {
        if (!ValidationUtil.validateEmail(sender)) {
            log.warn("Failed execute to senSimpleEmail, {} not a email.", sender);
            return;
        }
        //获取MimeMessage对象
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true);
            //邮件发送人
            messageHelper.setFrom(sender);
            messageHelper.setTo(receivers.stream().filter(ValidationUtil::validateEmail).distinct().toArray(String[]::new));
            //邮件主题
            message.setSubject(subject);
            //邮件内容，html格式
            messageHelper.setText(content, true);
            //发送
            javaMailSender.send(message);
        } catch (Throwable cause) {
            log.error("Failed execute to send email. receivers -> {}.", JsonUtil.toJson(receivers), cause);
        }
    }

    @Override
    public void sendRegistryEmail(String to, String receiver, String emailCode) {
        EmailTemplateInfo emailTemplate = AccountEmailTemplateUtil.getDefaultAccountRegistryEmailTemplate(receiver, emailCode);
        this.sendHtmlEmail(to, emailTemplate.getSubject(), emailTemplate.getContent());
    }
}
