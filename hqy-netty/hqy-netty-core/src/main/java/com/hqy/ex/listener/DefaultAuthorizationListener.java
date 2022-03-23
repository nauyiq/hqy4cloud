package com.hqy.ex.listener;

import com.hqy.ex.SocketProjectContext;
import com.hqy.socketio.HandshakeData;
import com.hqy.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * socket.io 扩展的http握手安全校验机制
 * @author qiyuan.hong
 * @date 2022-03-18 00:29
 */
public class DefaultAuthorizationListener implements AuthorizationListenerAdaptor {

    private static final Logger log = LoggerFactory.getLogger(DefaultAuthorizationListener.class);

    @Override
    public boolean isAuthorized(HandshakeData data) {
        //安全验证策略 jwt token机制
        String accessToken = data.getAccessToken();
        if (StringUtils.isBlank(accessToken) || JwtUtil.validate(accessToken)) {
            //安全校验策略不通过
            log.warn("@@@ Handshake isAuthorized failure, not found token or token invalid.");
            return false;
        } else {
            if (StringUtils.isEmpty(data.getBizId())) {
                //业务增强 设置业务id bizId
                SocketProjectContext context = JwtUtil.unSign(accessToken, SocketProjectContext.class);
                if (Objects.isNull(context)) {
                    log.warn("@@@ Handshake isAuthorized failure. unSign jwt token 2 SocketProjectContext class failure.");
                    return false;
                }
                data.setBizId(context.getBizId());
            }
        }
        return true;
    }
}
