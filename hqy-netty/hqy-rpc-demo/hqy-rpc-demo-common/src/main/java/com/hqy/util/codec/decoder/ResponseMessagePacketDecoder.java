package com.hqy.util.codec.decoder;

import com.google.common.collect.Maps;
import com.hqy.util.ByteBufferUtils;
import com.hqy.util.enums.MessageType;
import com.hqy.util.dto.ResponseMessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

/**
 * @author qy
 * @description:
 * @project: hqy-parent
 * @create 2021-07-09 10:02
 */
public class ResponseMessagePacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        ResponseMessagePacket packet = new ResponseMessagePacket();
        // 魔数
        packet.setMagicNumber(byteBuf.readInt());
        // 版本
        packet.setVersion(byteBuf.readInt());
        // 流水号
        int serialNumberLength = byteBuf.readInt();
        packet.setSerialNumber(byteBuf.readCharSequence(serialNumberLength, CharsetUtil.UTF_8).toString());
        // 消息类型
        byte messageTypeByte = byteBuf.readByte();
        packet.setMessageType(MessageType.fromValue(messageTypeByte));
        // 附件
        Map<String, String> attachments = Maps.newHashMap();
        int attachmentSize = byteBuf.readInt();
        if (attachmentSize > 0) {
            for (int i = 0; i < attachmentSize; i++) {
                int keyLength = byteBuf.readInt();
                String key = byteBuf.readCharSequence(keyLength, CharsetUtil.UTF_8).toString();
                int valueLength = byteBuf.readInt();
                String value = byteBuf.readCharSequence(valueLength, CharsetUtil.UTF_8).toString();
                attachments.put(key, value);
            }
        }
        packet.setAttachments(attachments);
        // error code
        packet.setErrorCode(byteBuf.readLong());
        // message
        String message = packet.getMessage();
        ByteBufferUtils.X.encodeUtf8CharSequence(byteBuf, message);
        // payload - ByteBuf实例
        int payloadLength = byteBuf.readInt();
        packet.setPayload(byteBuf.readBytes(payloadLength));
        list.add(packet);

    }
}
