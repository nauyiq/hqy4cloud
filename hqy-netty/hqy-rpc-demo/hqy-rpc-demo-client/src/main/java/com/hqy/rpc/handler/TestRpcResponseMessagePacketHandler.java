package com.hqy.rpc.handler;

import com.hqy.util.JsonUtil;
import com.hqy.util.codec.FastJsonSerializer;
import com.hqy.util.dto.ResponseMessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qy
 * @description:
 * @project: hqy-parent-all
 * @create 2021-07-09 11:27
 */
@Slf4j
public class TestRpcResponseMessagePacketHandler extends SimpleChannelInboundHandler<ResponseMessagePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ResponseMessagePacket packet) throws Exception {

        Object payload = packet.getPayload();

        if (payload instanceof ByteBuf) {

            Object targetPayload = packet.getPayload();
            if (targetPayload instanceof ByteBuf) {
                ByteBuf byteBuf = (ByteBuf) targetPayload;
                int readableByteLength = byteBuf.readableBytes();
                byte[] bytes = new byte[readableByteLength];
                byteBuf.readBytes(bytes);
                targetPayload = FastJsonSerializer.getInstance().decode(bytes, String.class);
                byteBuf.release();
            }
            packet.setPayload(targetPayload);
            log.info("接收到来自服务端的响应消息,消息内容:{}", JsonUtil.toJson(packet));

        }



    }
}
