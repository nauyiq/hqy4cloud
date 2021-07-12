package com.hqy.rpc;

import com.hqy.rpc.handler.TestRpcResponseMessagePacketHandler;
import com.hqy.util.codec.decoder.ResponseMessagePacketDecoder;
import com.hqy.util.codec.encoder.RequestMessagePacketEncoder;
import com.hqy.util.dto.RequestMessagePacket;
import com.hqy.util.enums.MessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qy
 * @description:
 * @project: hqy-parent
 * @create 2021-07-09 10:24
 */
@Slf4j
public class TestProtocolClient {

    public static void main(String[] args) {

        int port = 9192;

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new RequestMessagePacketEncoder());
                            pipeline.addLast(new ResponseMessagePacketDecoder());
                            pipeline.addLast(new TestRpcResponseMessagePacketHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect("localhost", port).sync();
            log.info("启动NettyClient[{}]成功...", port);
            Channel channel = future.channel();
            RequestMessagePacket packet = new RequestMessagePacket();
            packet.setMagicNumber(0);
            packet.setVersion(1);
            packet.setSerialNumber("v10241024");
            packet.setMessageType(MessageType.REQUEST);
            packet.setInterfaceName("club.throwable.contract.HelloService");
            packet.setMethodArgumentSignatures(new String[]{"java.lang.String"});
            packet.setMethodArguments(new Object[]{"doge"});
            channel.writeAndFlush(packet);
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("#### rpc-demo客户端服务启动失败...");
            log.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
        }



    }

}
