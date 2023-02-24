package com.hqy.socketio;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * AbstractServerLauncher.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 14:04
 */
public abstract class AbstractServerLauncher implements ServerLauncher {
    private static final Logger log = LoggerFactory.getLogger(AbstractServerLauncher.class);

    private final boolean enableSsl;
    private final boolean randomSession;
    private final String keystore;
    private final String password;

    protected AbstractServerLauncher() {
        this(false, true, StringConstants.EMPTY, StringConstants.EMPTY);
    }

    protected AbstractServerLauncher(boolean enableSsl, boolean randomSession, String keystore, String password) {
        this.enableSsl = enableSsl;
        this.randomSession = randomSession;
        this.keystore = keystore;
        this.password = password;
    }

    @Override
    public final SocketIOServer startUp(int port, String contextPath, AuthorizationListener authorizationListener) throws Exception {
        log.info("EventLauncher Start up to registry SocketIoServer, port: {}, contextPath: {}.", port, contextPath);

        //new socket.io configuration.
        Configuration configuration = new Configuration();
        configuration.setAuthorizationListener(authorizationListener);
        configuration.setContext(contextPath);
        configuration.setPort(port);
        configuration.setRandomSession(randomSession);

        if (enableSsl) {
            loadSslKeyStore(configuration);
        } else {
            log.info("Concurrent environment disable ssl.");
        }

        SocketIOServer socketIOServer = new SocketIOServer(configuration);
        //socket.io enhance.
        enhanceSocketIoServer(socketIOServer);
        //registry socketIoServet to ProjectContextInfo
        ProjectContextInfo.setBean(socketIOServer);
        SpringContextHolder.getProjectContextInfo().registrySocketIoPort(port);
        //start up socketIoServer
        socketIOServer.start();

        return socketIOServer;
    }


    /**
     * enhance SocketIoServer.
     * @param socketIOServer {@link SocketIOServer}
     * @throws Exception exception.
     */
    protected abstract void enhanceSocketIoServer(SocketIOServer socketIOServer) throws Exception;




    private void loadSslKeyStore(Configuration configuration) {
        log.info("@@@ 非开发环境需要加载keystore, keystore:{}", keystore);
        configuration.setKeyStorePassword(password);
        try (InputStream inputStream = SocketIOServer.class.getResourceAsStream(keystore)){
            configuration.setKeyStore(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public boolean isRandomSession() {
        return randomSession;
    }

    public String getKeystore() {
        return keystore;
    }

    public String getPassword() {
        return password;
    }
}
