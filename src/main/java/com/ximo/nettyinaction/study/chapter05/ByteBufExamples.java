package com.ximo.nettyinaction.study.chapter05;

import io.netty.buffer.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ByteProcessor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author xikl
 * @date 2019/7/21
 */
@Slf4j
public class ByteBufExamples {

    private static final ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

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

    /**
     * @see  ByteBuf#capacity()
     * @see ByteBuf#writerIndex(int)
     *
     */
    public static void byteBufRelativeAccess() {
        ByteBuf byteBuf = BYTE_BUF_FROM_SOMEWHERE;

        // 读写数据 不会变更 readerIndex 也不会改变writerIndex
        for (int i = 0, capacity = byteBuf.capacity(); i < capacity; i++) {
            final byte b = byteBuf.getByte(i);
            log.info("byte: {}", b);
        }
    }

    // 虽然 ByteBuf 同时具有读索引和写索引，但是 JDK 的 ByteBuffer 却只有一个索引，这
    // 也就是为什么必须调用 flip()方法来在读模式和写模式之间进行切换的原因


    /**
     * 虽然你可能会倾向于频繁地调用 discardReadBytes()方法以确保可写分段的最大化，但是
     * 请注意，这将极有可能会导致内存复制，因为可读字节（图中标记为 CONTENT 的部分）必须被移
     * 动到缓冲区的开始位置。我们建议只在有真正需要的时候才这样做，例如，当内存非常宝贵的时候。
     *
     */
    public static void discardReadBytes() {
        ByteBuf byteBuf = BYTE_BUF_FROM_SOMEWHERE;

        // 回收
        final ByteBuf byteBuf1 = byteBuf.discardReadBytes();
    }

    /**
     * 读取所有数据
     */
    public static void readAllData() {
        ByteBuf byteBuf = BYTE_BUF_FROM_SOMEWHERE;

        while (byteBuf.isReadable()) {
            log.info("数据：{}", byteBuf.readByte());
        }
    }

    public static void write() {
        ByteBuf byteBuf = BYTE_BUF_FROM_SOMEWHERE;
        while (byteBuf.writableBytes() >= 4) {
            byteBuf.writeInt(ThreadLocalRandom.current().nextInt());
        }
    }

    /**
     * @see io.netty.util.ByteProcessor
     */
    public static void byteProcessor() {
        ByteBuf byteBuf = BYTE_BUF_FROM_SOMEWHERE;
        final int indexCr = byteBuf.forEachByte(ByteProcessor.FIND_CR);
        final int indexCrlf = byteBuf.forEachByte(ByteProcessor.FIND_CRLF);
    }

    /**
     * 共享的，同一个内存地址
     *
     */
    public static void byteBufSlice() {
        final ByteBuf byteBuf = Unpooled.copiedBuffer("Netty in action !!!!!".getBytes(StandardCharsets.UTF_8));

        final ByteBuf slice = byteBuf.slice(0, 15);
        log.info("slice: {}", slice.toString(StandardCharsets.UTF_8));

        // 赋值
        byteBuf.setByte(0, (byte)'J');

        // 结果为真
        assert byteBuf.getByte(0) == slice.getByte(0);
    }


    /**
     * 复制一个 ByteBuf, 不一样的结果
     */
    public static void byteBufCopy() {
        //创建 ByteBuf 以保存所提供的字符串的字节
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", StandardCharsets.UTF_8);
        //创建该 ByteBuf 从索引 0 开始到索引 15 结束的分段的副本
        ByteBuf copy = buf.copy(0, 15);
        //将打印“Netty in Action”
        System.out.println(copy.toString(StandardCharsets.UTF_8));
        //更新索引 0 处的字节
        buf.setByte(0, (byte)'J');
        //将会成功，因为数据不是共享的
        assert buf.getByte(0) != copy.getByte(0);
    }

    /**
     * get 和 set 方法不会修改 readerIndex 和 writerIndex
     */
    public static void byteBufSetGet() {
        //创建一个新的 ByteBuf以保存给定字符串的字节
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", StandardCharsets.UTF_8);
        //打印第一个字符'N'
        log.info("第一个字符：{}", (char)buf.getByte(0));
        //存储当前的 readerIndex 和 writerIndex
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        //将索引 0 处的字 节更新为字符'B'
        buf.setByte(0, (byte)'B');
        //打印第一个字符，现在是'B'
        log.info("更新为B后{}", (char)buf.getByte(0));
        //将会成功，因为这些操作并不会修改相应的索引
        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();
    }

    public static void byteBufWriteRead() {
        final ByteBuf byteBuf = Unpooled.copiedBuffer("Netty in action!!!".getBytes(StandardCharsets.UTF_8));
         byte b = byteBuf.readByte();
        final int readerIndex = byteBuf.readerIndex();
        final int writerIndex = byteBuf.writerIndex();
        log.info("读取第一个字符{}， readIndex:{}, writerIndex: {}", b, readerIndex, writerIndex);

        // 写入：index会变化
        // 将字符 '?'追加到缓冲区
        final ByteBuf writeByte = byteBuf.writeByte((byte) 'Y');
        final int readerIndex2 = byteBuf.readerIndex();
        final int writerIndex2 = byteBuf.writerIndex();
        log.info("写入第一个字符{}， readerIndex2:{}, writerIndex2: {}", writeByte.toString(StandardCharsets.UTF_8), readerIndex2, writerIndex2);
    }


    public static void byteBufHolderTest() {
        ByteBufHolder byteBufHolder = new DefaultByteBufHolder(BYTE_BUF_FROM_SOMEWHERE);
        final ByteBuf content = byteBufHolder.content();

        // 深拷贝
        final ByteBufHolder copy = byteBufHolder.copy();
        // 浅拷贝
        final ByteBufHolder duplicate = byteBufHolder.duplicate();
    }

    // byteBuf 的分配
    public static void obtainingByteBufAllocatorReference() {
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        // 获得分配器
        final ByteBufAllocator alloc = channel.alloc();

        ChannelHandlerContext channelHandlerContext = null;

        // 似乎没有办法拿到这个值
        final ByteBufAllocator alloc1 = channelHandlerContext.alloc();
    }

    // 很棒的工具类
    public static void byteBufUtilTest() {
        final byte[] bytes = ByteBufUtil.getBytes(BYTE_BUF_FROM_SOMEWHERE);
    }


    public static void referenceCounting() {
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        final ByteBufAllocator alloc = channel.alloc();
        final ByteBuf byteBuf = alloc.directBuffer();
        // 是否引用次数为1
        assert byteBuf.refCnt() == 1;
    }

    public static void releaseReferenceCountedObject() {
        ByteBuf byteBuf = BYTE_BUF_FROM_SOMEWHERE;
        // 是否成功
        final boolean release = byteBuf.release();
    }
}
