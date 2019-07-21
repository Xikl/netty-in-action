package com.ximo.nettyinaction.chapter05;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;


/**
 * @author xikl
 * @date 2019/7/21
 */
@Slf4j
public class ByteBufExamples {

    private static final ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

//    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE =

    /**
     * 处理一个数组的方法
     *
     * @param array
     * @param offset
     * @param len
     */
    private static void handleArray(byte[] array, int offset, int len) {
        log.info("the array {} offset: {} length: {}", array, offset, len);
    }

    /**
     * backing array
     * 此处参考学习一下 java 原始 api ByteBuffer 的使用
     *
     * @link <a href="https://github.com/LingCoder/OnJava8/blob/master/docs/book/Appendix-New-IO.md"></>
     */
    public static void heapBuufer() {
        ByteBuf heapBuf = BYTE_BUF_FROM_SOMEWHERE;

        if (heapBuf.hasArray()) {
            final byte[] array = heapBuf.array();
            // 获得计算第一个字节的偏移量
            final int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
            // 获得可以读取的字节数
            final int length = heapBuf.readableBytes();
            //使用数组、偏移量和长度作为参数调用你的方法
            handleArray(array, offset, length);
        }
    }

    /**
     * 直接缓冲区的内容将驻留在常规的会被垃圾回收的堆
     * 之外。”这也就解释了为何直接缓冲区对于网络数据传输是理想的选择。如果你的数据包含在一
     * 个在堆上分配的缓冲区中，那么事实上，在通过套接字发送它之前，JVM将会在内部把你的缓冲
     * 区复制到一个直接缓冲区中。
     * 会有点慢 要多测试一下
     */
    public static void directBuffer() {
        ByteBuf directBuf = BYTE_BUF_FROM_SOMEWHERE;
        // 如果没有数组支持 那么他就是一个直接缓冲区
        if (!directBuf.hasArray()) {
            final int length = directBuf.readableBytes();
            final byte[] byteArray = new byte[length];

            // 将数据拷贝到数组中 是一个backing array的buffer
            final ByteBuf newByteBufWithBackingArray =
                    directBuf.getBytes(directBuf.readerIndex(), byteArray);
            log.info("the new ByteBuf has Array: {} ", newByteBufWithBackingArray.hasArray());
            handleArray(byteArray, 0, length);
        }
    }

    /**
     * 使用原始api来进行操作
     *
     * @param header 头
     * @param body   主体
     */
    public static void byteBufferComposite(ByteBuffer header, ByteBuffer body) {
        ByteBuffer[] byteBufferMessageAyyay = new ByteBuffer[]{header, body};
        // 分配header和body的剩余空间 remain 剩余
        ByteBuffer byteBufferMessage = ByteBuffer.allocate(header.remaining() + body.remaining());

        // 分别 put头和尾 然后反转channel
        byteBufferMessage.put(header)
                .put(body)
                .flip();
    }

    /**
     * 复合缓存区模式 with ByteBuf
     *
     * @see CompositeByteBuf#addComponents(ByteBuf...)
     * @see CompositeByteBuf#removeComponent(int)
     * @see ByteBuf
     */
    public static void ByteBufComposite() {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();

        ByteBuf headerBuf = BYTE_BUF_FROM_SOMEWHERE;
        ByteBuf bodyBuf = BYTE_BUF_FROM_SOMEWHERE;

        // 添加多个ByteBuf 等同于ByteBuffer中的put方法
        messageBuf.addComponents(headerBuf, bodyBuf);

        // other

        // 删除索引为0的byteBuf（第一个）
        messageBuf.removeComponent(0);

        for (ByteBuf byteBuf : messageBuf) {
            log.info("byte buf {}", byteBuf);
        }
    }

    public static void byteBufCompositeArray() {
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        // 获得可读字节数
        final int length = compositeByteBuf.readableBytes();
        // 一个新的array
        byte[] array = new byte[length];
        // 将字节读取到数组中
        compositeByteBuf.getBytes(compositeByteBuf.readerIndex(), array);
        handleArray(array, 0, length);
    }

}
