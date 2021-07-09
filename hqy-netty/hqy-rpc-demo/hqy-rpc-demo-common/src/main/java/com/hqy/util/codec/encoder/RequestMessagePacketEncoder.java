package com.hqy.util.codec.encoder;

import com.hqy.util.codec.Serializer;
import com.hqy.util.dto.RequestMessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author qy
 * @description: RequestMessagePacket编码器
 * @project: hqy-parent
 * @create 2021-07-08 19:41
 */
@RequiredArgsConstructor
public class RequestMessagePacketEncoder extends MessageToByteEncoder<RequestMessagePacket> {

    private final Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestMessagePacket requestMessagePacket, ByteBuf byteBuf) throws Exception {

        byteBuf.writeInt(requestMessagePacket.getMagicNumber());

        byteBuf.writeInt(requestMessagePacket.getVersion());

        byteBuf.writeInt(requestMessagePacket.getSerialNumber().length());
        byteBuf.writeCharSequence(requestMessagePacket.getSerialNumber(), CharsetUtil.UTF_8);

        byteBuf.writeByte(requestMessagePacket.getMessageType().type);

        Map<String, String> attachments = requestMessagePacket.getAttachments();
        attachments.forEach((k, v) -> {
            byteBuf.writeInt(k.length());
            byteBuf.writeCharSequence(k, CharsetUtil.UTF_8);
            byteBuf.writeInt(v.length());
            byteBuf.writeCharSequence(v, CharsetUtil.UTF_8);
        });

        byteBuf.writeInt(requestMessagePacket.getInterfaceName().length());
        byteBuf.writeCharSequence(requestMessagePacket.getInterfaceName(), CharsetUtil.UTF_8);

        byteBuf.writeInt(requestMessagePacket.getMethodName().length());
        byteBuf.writeCharSequence(requestMessagePacket.getMethodName(), CharsetUtil.UTF_8);

        // 方法参数签名(String[]类型) - 非必须
        if (null != requestMessagePacket.getMethodArgumentSignatures()) {
            int len = requestMessagePacket.getMethodArgumentSignatures().length;
            // 方法参数签名数组长度
            byteBuf.writeInt(len);
            for (int i = 0; i < len; i++) {
                String methodArgumentSignature = requestMessagePacket.getMethodArgumentSignatures()[i];
                byteBuf.writeInt(methodArgumentSignature.length());
                byteBuf.writeCharSequence(methodArgumentSignature, CharsetUtil.UTF_8);
            }
        } else {
            byteBuf.writeInt(0);
        }

        // 方法参数(Object[]类型) - 非必须
        if (null != requestMessagePacket.getMethodArguments()) {
            int len = requestMessagePacket.getMethodArguments().length;
            // 方法参数数组长度
            byteBuf.writeInt(len);
            for (int i = 0; i < len; i++) {
                byte[] bytes = serializer.encode(requestMessagePacket.getMethodArguments()[i]);
                byteBuf.writeInt(bytes.length);
                byteBuf.writeBytes(bytes);
            }
        } else {
            byteBuf.writeInt(0);
        }


    }
}
