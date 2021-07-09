package com.hqy.util.codec.encoder;

import com.hqy.util.codec.Serializer;
import com.hqy.util.dto.ResponseMessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author qy
 * @description:
 * @project: hqy-parent
 * @create 2021-07-09 9:55
 */
@RequiredArgsConstructor
public class ResponseMessagePacketEncoder extends MessageToByteEncoder<ResponseMessagePacket> {

    private final Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessagePacket responseMessagePacket, ByteBuf byteBuf) throws Exception {
        // 魔数
        byteBuf.writeInt(responseMessagePacket.getMagicNumber());
        // 版本
        byteBuf.writeInt(responseMessagePacket.getVersion());
        // 流水号
        byteBuf.writeInt(responseMessagePacket.getSerialNumber().length());
        byteBuf.writeCharSequence(responseMessagePacket.getSerialNumber(), CharsetUtil.UTF_8);
        // 消息类型
        byteBuf.writeByte(responseMessagePacket.getMessageType().getType());
        // 附件size
        Map<String, String> attachments = responseMessagePacket.getAttachments();
        byteBuf.writeInt(attachments.size());
        // 附件内容
        attachments.forEach((k, v) -> {
            byteBuf.writeInt(k.length());
            byteBuf.writeCharSequence(k, CharsetUtil.UTF_8);
            byteBuf.writeInt(v.length());
            byteBuf.writeCharSequence(v, CharsetUtil.UTF_8);
        });
        // error code
        byteBuf.writeLong(responseMessagePacket.getErrorCode());
        // message
        String message = responseMessagePacket.getMessage();
        byteBuf.writeCharSequence(message, CharsetUtil.UTF_8);
//        ByteBufferUtils.X.encodeUtf8CharSequence(byteBuf, message);
        // payload
        byte[] bytes = serializer.encode(responseMessagePacket.getPayload());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
