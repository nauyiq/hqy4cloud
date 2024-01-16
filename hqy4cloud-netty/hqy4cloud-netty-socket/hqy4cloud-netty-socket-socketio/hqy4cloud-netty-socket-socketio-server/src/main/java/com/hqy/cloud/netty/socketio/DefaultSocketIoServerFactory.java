package com.hqy.cloud.netty.socketio;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.netty.socketio.deloyer.SocketIoServerModel;
import com.hqy.cloud.netty.socketio.listener.DefaultAuthorizationListener;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.hqy.cloud.netty.socketio.SocketIoConstants.*;

/**
 * AbstractSocketIoServerFactory.
 * @see com.hqy.cloud.netty.socketio.SocketIoServerFactory
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public class DefaultSocketIoServerFactory implements SocketIoServerFactory  {
    private static final Logger log = LoggerFactory.getLogger(DefaultSocketIoServerFactory.class);

    private final SocketIoServerModel socketIoServerModel;
    public DefaultSocketIoServerFactory(SocketIoServerModel socketIoServerModel) {
        AssertUtil.notNull(socketIoServerModel, "SocketIo model should not be null.");
        this.socketIoServerModel = socketIoServerModel;
    }

    @Override
    public SocketIOServer createSocketIoServer() {
        //new socket.io configuration.
        Configuration configuration = new Configuration();
        configuration.setPort(socketIoServerModel.getPort());
        configuration.setContext(socketIoServerModel.getContext());
        configuration.setAuthorizationListener(new DefaultAuthorizationListener(socketIoServerModel.getAuthorizationService()));
        // is enable ssl.
        boolean enableSsl = socketIoServerModel.getParameter(SOCKET_SERVER_IO_ENABLED_SSL, DEFAULT_SOCKET_SERVER_IO_ENABLED_SSL);
        if (enableSsl) {
            loadSslKeyStore(configuration);
        } else {
            log.info("SocketIo server disable ssl.");
        }
        // is random session
        boolean randomSession = socketIoServerModel.getParameter(SOCKET_IO_RANDOM_SESSION, DEFAULT_SOCKET_IO_RANDOM_SESSION);
        configuration.setRandomSession(randomSession);
        return new SocketIOServer(configuration);
    }

    private void loadSslKeyStore(Configuration configuration) {
        String keystore = socketIoServerModel.getParameter(SOCKET_SERVER_IO_SERVER_SSL_KEYSTORE);
        String password = socketIoServerModel.getParameter(SOCKET_SERVER_IO_SERVER_SSL_KEYSTORE_PASSWORD);
        configuration.setKeyStorePassword(password);
        try {
           if (StringUtils.isNotBlank(keystore)) {
               InputStream inputStream = SocketIOServer.class.getResourceAsStream(keystore);
               configuration.setKeyStore(inputStream);
           }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }

    }
}
