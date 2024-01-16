package com.hqy.cloud.netty.socketio.listener;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.socket.model.SocketAuthorization;
import com.hqy.foundation.authorization.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Using AuthorizationService check HandshakeData.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public record DefaultAuthorizationListener(
        AuthorizationService authorizationService) implements AuthorizationListener {
    private final static Logger log = LoggerFactory.getLogger(DefaultAuthorizationListener.class);


    @Override
    public boolean isAuthorized(HandshakeData data) {
        String accessToken = data.getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        SocketAuthorization socketAuthorization = authorizationService.decryptAuthorization(accessToken, SocketAuthorization.class);
        if (socketAuthorization == null) {
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.warn("Authorization socketio handshake token failure, token {}", accessToken);
            }
            return false;
        }
        data.setBizId(socketAuthorization.getBizId());
        return true;
    }
}
