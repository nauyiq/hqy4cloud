package com.hqy.communication.service.mail;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.rpc.api.service.RPCService;

import java.util.Set;

/**
 * RPC REMOTE EMAIL SERVICE.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 14:57
 */
@ThriftService(MicroServiceConstants.COMMUNICATION_SERVICE)
public interface EmailRemoteService extends RPCService {

    /**
     * 发送简单邮件.
     * @param to      发给谁.
     * @param subject 主题.
     * @param content 内容.
     */
    @ThriftMethod(oneway = true)
    void sendSimpleEmail(@ThriftField(1)String to, @ThriftField(2)String subject, @ThriftField(3)String content);


    /**
     * 发送简单邮件
     * @param sender   指定谁发送的.
     * @param to      发给谁.
     * @param subject 主题.
     * @param content 内容
     */
    @ThriftMethod(oneway = true)
    void senderSimpleEmail(@ThriftField(1)String sender, @ThriftField(2)String to, @ThriftField(3)String subject, @ThriftField(4)String content);

    /**
     * 群发-简单邮件
     * @param receivers 发给谁.
     * @param subject  主题.
     * @param content  内容.
     */
    @ThriftMethod(oneway = true)
    void sendSimpleEmails(@ThriftField(1)Set<String> receivers, @ThriftField(2)String subject, @ThriftField(3)String content);

    /**
     * 群发-简单邮件
     * @param sender   指定谁发送的.
     * @param receivers 发给谁
     * @param subject  主题
     * @param content  内容
     */
    @ThriftMethod(oneway = true)
    void senderSimpleEmails(@ThriftField(1)String sender, @ThriftField(2)Set<String> receivers, @ThriftField(3)String subject, @ThriftField(4)String content);

    /**
     * 发送html模板邮件
     * @param to      发给谁
     * @param subject 主题
     * @param content 内容
     */
    @ThriftMethod(oneway = true)
    void sendHtmlEmail(@ThriftField(1)String to, @ThriftField(2)String subject, @ThriftField(3)String content);

    /**
     * 群发-html模板邮件
     * @param receivers 发给谁
     * @param subject  主题
     * @param content  内容
     */
    @ThriftMethod(oneway = true)
    void sendHtmlEmails(@ThriftField(1)Set<String> receivers, @ThriftField(2)String subject, @ThriftField(3)String content);

    /**
     * 发送html模板邮件
     * @param sender  发送者
     * @param to      发给谁
     * @param subject 主题
     * @param content 内容
     */
    @ThriftMethod(oneway = true)
    void senderHtmlEmail(@ThriftField(1)String sender, @ThriftField(2)String to, @ThriftField(3)String subject, @ThriftField(4)String content);

    /**
     * 群发-html模板邮件
     * @param sender    发送者
     * @param receivers 发给谁
     * @param subject   主题
     * @param content   内容
     */
    @ThriftMethod(oneway = true)
    void senderHtmlEmails(@ThriftField(1)String sender, @ThriftField(2)Set<String> receivers, @ThriftField(3)String subject, @ThriftField(4)String content);

    /**
     * 发送-注册邮件.
     * @param to          发给谁.
     * @param receiver    接收者用户名
     * @param emailCode   邮箱验证码.
     */
    @ThriftMethod(oneway = true)
    void sendRegistryEmail(@ThriftField(1) String to, @ThriftField(2)String receiver, @ThriftField(3)String emailCode);

}
