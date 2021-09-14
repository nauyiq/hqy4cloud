package com.hqy.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Netty 的促进 ChannelHandler 的测试通过的所谓“嵌入式”传输。这是由一个特殊 Channel 实现,EmbeddedChannel,它提供了一个简单的方法通过管道传递事件。
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-15 9:48
 */

public class TestChannelHandler {

    // 测试入站消息 begin

     static class FixedLengthFrameDecoder extends ByteToMessageDecoder { // 继承 ByteToMessageDecoder 用来处理入站的字节并将他们解码为消息
        private final int frameLength;

        public FixedLengthFrameDecoder(int frameLength) { // 指定产出的帧的长度
            if (frameLength <= 0) {
                throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
            }
            this.frameLength = frameLength;
        }

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
            if (byteBuf.readableBytes() >= frameLength) { //检查是否有足够的字节用于读到下个帧
                ByteBuf buf = byteBuf.readBytes(frameLength); //从 ByteBuf 读取新帧
                list.add(buf); //添加帧到解码好的消息 List
            }
        }

    }

    @Test
    public void testFrameDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
        Assert.assertTrue(channel.writeInbound(input.readBytes(7)));

        Assert.assertTrue(channel.finish());
        ByteBuf read = channel.readInbound(); //每次读三个字节
        Assert.assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), read);
        read.release();

        Assert.assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
        Assert.assertTrue(channel.writeInbound(input.readBytes(7)));

        Assert.assertTrue(channel.finish());
        ByteBuf read = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), read);
        read.release();

        Assert.assertNull(channel.readInbound());
        buf.release();
    }

    //测试入站消息 end


    //测试出站消息
    static class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
        @Override
        protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
            while (in.readableBytes() >= 4) { //检查是否有足够的字节用于编码
                int value = Math.abs(in.readInt());//读取下一个输入 ByteBuf 产出的 int 值，并计算绝对值
                out.add(value);  //写 int 到编码的消息 List
            }
        }
    }

    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        Assert.assertTrue(channel.writeOutbound(buf)); //4

        Assert.assertTrue(channel.finish());

        for (int i = 1; i < 10; i++) {
            Assert.assertEquals(i, (int)channel.readOutbound());  //6
        }
        Assert.assertNull(channel.readOutbound());
    }

}
