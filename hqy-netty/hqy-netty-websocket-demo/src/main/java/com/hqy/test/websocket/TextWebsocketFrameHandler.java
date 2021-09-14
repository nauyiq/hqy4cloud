package com.hqy.test.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * websocket协议定义了六种不同的协议帧 (https://blog.csdn.net/xmcy001122/article/details/117226953)
 * 在netty里面分别对应类,BinaryWebSocketFrame、TextWebSocketFrame、ContinuationWebSocketFrame、CloseWebSocketFrame、PingWebSocketFrame、PongWebSocketFrame
 * 这里只处理TextWebSocketFrame,其他的交由Netty提供的WebSocketServerProtocolHandler自动处理
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-15 16:25
 */
@SuppressWarnings("deprecation")
public class TextWebsocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public TextWebsocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception { //覆写userEventTriggered() 方法来处理自定义事件
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //如果接收的事件表明握手成功,就从 ChannelPipeline 中删除HttpRequestHandler ，因为接下来不会接受 HTTP 消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            //写一条消息给所有的已连接 WebSocket 客户端，通知它们建立了一个新的 Channel 连接
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            //添加新连接的 WebSocket Channel 到 ChannelGroup 中，这样它就能收到所有的信息
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }


    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        group.writeAndFlush(textWebSocketFrame.retain());   //保留收到的消息，并通过 writeAndFlush() 传递给所有连接的客户端。
    }
}
