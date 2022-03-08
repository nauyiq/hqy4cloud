package com.hqy.netty.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * websocket处理的handler 基于RFC 6455文档开发;
 * https://datatracker.ietf.org/doc/rfc6455/
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 15:30
 */
public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
