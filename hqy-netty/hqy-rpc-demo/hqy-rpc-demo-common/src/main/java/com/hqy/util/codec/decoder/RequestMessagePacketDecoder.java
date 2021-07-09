package com.hqy.util.codec.decoder;

import com.google.common.collect.Maps;
import com.hqy.util.enums.MessageType;
import com.hqy.util.dto.RequestMessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author qy
 * @description: RequestMessagePacket解码器
 * @project: hqy-parent
 * @create 2021-07-09 9:33
 */
@RequiredArgsConstructor
public class RequestMessagePacketDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        RequestMessagePacket packet = new RequestMessagePacket();

        packet.setMagicNumber(byteBuf.readInt());

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

        // 接口全类名
        int interfaceNameLength = byteBuf.readInt();
        packet.setInterfaceName(byteBuf.readCharSequence(interfaceNameLength, CharsetUtil.UTF_8).toString());

        // 方法名
        int methodNameLength = byteBuf.readInt();
        packet.setMethodName(byteBuf.readCharSequence(methodNameLength, CharsetUtil.UTF_8).toString());

        // 方法参数签名
        int methodArgumentSignatureArrayLength = byteBuf.readInt();
        if (methodArgumentSignatureArrayLength > 0) {
            String[] methodArgumentSignatures = new String[methodArgumentSignatureArrayLength];
            for (int i = 0; i < methodArgumentSignatureArrayLength; i++) {
                int methodArgumentSignatureLength = byteBuf.readInt();
                methodArgumentSignatures[i] = byteBuf.readCharSequence(methodArgumentSignatureLength, CharsetUtil.UTF_8).toString();
            }
            packet.setMethodArgumentSignatures(methodArgumentSignatures);
        }

        // 方法参数
        int methodArgumentArrayLength = byteBuf.readInt();
        if (methodArgumentArrayLength > 0) {
            // 这里的Object[]实际上是ByteBuf[] - 后面需要二次加工为对应类型的实例
            Object[] methodArguments = new Object[methodArgumentArrayLength];
            for (int i = 0; i < methodArgumentArrayLength; i++) {
                int byteLength = byteBuf.readInt();
                methodArguments[i] = byteBuf.readBytes(byteLength);
            }
            packet.setMethodArguments(methodArguments);
        }
        list.add(packet);

    }
}
