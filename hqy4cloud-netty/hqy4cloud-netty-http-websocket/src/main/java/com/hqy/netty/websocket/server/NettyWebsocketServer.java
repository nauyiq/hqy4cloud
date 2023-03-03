package com.hqy.netty.websocket.server;

import com.hqy.netty.websocket.dto.SslKeystoreDTO;
import com.hqy.netty.websocket.handler.WebsocketHandler;
import com.hqy.netty.websocket.session.BaseWsSession;
import com.hqy.netty.websocket.util.SslUtil;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.thread.DefaultThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.ThreadFactory;

/**
 * 根据RFC 6455文档开发; https://datatracker.ietf.org/doc/rfc6455/
 * java netty实现的简单websocket服务器,支持二进制
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 13:28
 */
public abstract class NettyWebsocketServer {

    private static final Logger log = LoggerFactory.getLogger(NettyWebsocketServer.class);

    public NettyWebsocketServer() {
    }

    public NettyWebsocketServer(int bossThreads, int workerThreads) {
        this.bossThreads = bossThreads;
        this.workerThreads = workerThreads;
    }

    /**
     * keystore相关数据
     */
    private SslKeystoreDTO sslKeystore;

    /**
     * boss线程组 线程个数
     */
    private int bossThreads = Runtime.getRuntime().availableProcessors() <= 8 ? 1 : 4;

    /**
     * worker线程组 线程个数
     */
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;



    public void bind(int wsPort, boolean ssl) {
        log.info("@@@ Start wss server, ready to bind port: {}, ssl: {}", wsPort, ssl);
        log.info("@@@ Start wss server, bossThreads:{}, workerThreads:{}", bossThreads, workerThreads);

        //服务端通道class
        Class<? extends ServerChannel> channelClass;
        //boos线程组和工作线程组
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        //线程工厂
        ThreadFactory bossFactory = new DefaultThreadFactory("wsBossFactory");
        ThreadFactory workerFactory = new DefaultThreadFactory("wsWorkerFactory");

        if (ProjectContextInfo.isUseLinuxNativeEpoll()) {
            //使用linux epoll机制
            bossGroup = new EpollEventLoopGroup(bossThreads, bossFactory);
            workerGroup = new EpollEventLoopGroup(workerThreads, workerFactory);
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(bossThreads, bossFactory);
            workerGroup = new NioEventLoopGroup(workerThreads, workerFactory);
            channelClass = NioServerSocketChannel.class;
        }

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(channelClass)
                    /*
                        1.如果backlog过小，可能会出现accept速度跟不上，A.B 队列满了，导致新客户端无法连接，
                        要注意的是，backlog对程序支持的连接数并无影响，backlog影响的只是还没有被accept 取出的连接
                        2.websocket 服务器时不要打开SO_KEEPALIVE，更快些；
                        SO_KEEPALIVE连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接。
                     */
                    .option(ChannelOption.SO_BACKLOG, 1024 * 8)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            if (ssl) {
                                SSLContext sslContext =
                                        SslUtil.createSslContext(sslKeystore.keystoreType, sslKeystore.keystoreFilePath, sslKeystore.keystorePassword);
                                //SSLEngine 此类允许使用ssl安全套接层协议进行安全通信
                                SSLEngine sslEngine = sslContext.createSSLEngine();
                                sslEngine.setUseClientMode(false);
                                sslEngine.setWantClientAuth(false);
                                channel.pipeline().addLast(new SslHandler(sslEngine));
                            } else {
                                log.info("@@@ 未使用SSL安全通道, 建议使用SSL模式");

                                //TODO TCP连接计数？

                                //服务端对http请求的解码器
                                channel.pipeline().addLast( new HttpServerCodec());
                                //http报文的消息聚合器 因为http的是流失传输的 需要将http报文的数据组装成为封装好的httpRequest对象 最大长度为1M
                                channel.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                                //ChunkedWriteHandler来解决大文件或者码流传输过程中可能发生的内存溢出问题。
                                channel.pipeline().addLast(new ChunkedWriteHandler());
                                //自定义业务handler
                                channel.pipeline().addLast(new WebsocketHandler(getWsSessionClass()));
                            }
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(wsPort).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    /**
     * 由子类初始化需要使用加密的 keystore数据
     * @param sslKeystore SslKeystoreDTO
     */
    abstract void setSslKeystore(SslKeystoreDTO sslKeystore);

    /**
     * 又子类构造
     * @return
     */
    abstract Class<? extends BaseWsSession> getWsSessionClass();


}
