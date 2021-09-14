package com.hqy.test.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * 引导启动websocket服务..
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-15 16:55
 */
public class WebsocketServer {

    //创建 DefaultChannelGroup 用来 保存所有连接的的 WebSocket channel
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private final EventLoopGroup group = new NioEventLoopGroup();

    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup));

        ChannelFuture channelFuture = bootstrap.bind(address);
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
        return channelFuture;
    }

    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        return new WebsocketPipelineInitializer(group);
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 8090;
        WebsocketServer server = new WebsocketServer();
        ChannelFuture future = server.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(server::destroy));
        future.channel().closeFuture().syncUninterruptibly();

    }


}
