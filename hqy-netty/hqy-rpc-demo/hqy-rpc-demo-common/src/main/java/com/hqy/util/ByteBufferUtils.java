package com.hqy.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public enum ByteBufferUtils {

    // 单例
    X;

    public void encodeUtf8CharSequence(ByteBuf byteBuf, CharSequence charSequence) {
        int writerIndex = byteBuf.writerIndex();
        byteBuf.writeInt(0);
        int length = ByteBufUtil.writeUtf8(byteBuf, charSequence);
        byteBuf.setInt(writerIndex, length);
    }
}