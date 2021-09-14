package com.hqy.test.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化websocket自定义的pipeline 并实现自定义的websocket-handler
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-15 16:45
 */
public class WebsocketPipelineInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public WebsocketPipelineInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec()) // 添加http的编解码器
                .addLast(new HttpObjectAggregator(64 * 1024)) //添加http消息聚合器 最大长度为64k
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpRequestHandler("/ws")) //添加自定义的requestHandler 这里采用ws协议
                .addLast(new WebSocketServerProtocolHandler("/ws")) //WebSocketServerProtocolHandler 处理所有规定的 WebSocket 帧类型和升级握手本身。
                .addLast(new TextWebsocketFrameHandler(group));
    }
}
