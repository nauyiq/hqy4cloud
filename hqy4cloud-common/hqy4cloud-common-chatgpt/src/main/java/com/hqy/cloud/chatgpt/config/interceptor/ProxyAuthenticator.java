package com.hqy.cloud.chatgpt.config.interceptor;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 14:04
 */
public class ProxyAuthenticator extends Authenticator {
    private final PasswordAuthentication authentication;
    public ProxyAuthenticator(String username, String password) {
        this.authentication = new PasswordAuthentication(username, password.toCharArray());
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return this.authentication;
    }

}
