package com.ximo.nettyinaction.chapter04;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * channel的操作例子
 *
 * @author xikl
 * @date 2019/7/17
 */
@Slf4j
public class ChannelOperationExamples {

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();


    public static void writingToChannel() {

        // //创建持有要写数据的 ByteBuf
        final ByteBuf byteBuf = Unpooled.copiedBuffer("Hello".getBytes(StandardCharsets.UTF_8));

        // 写入 然后冲刷
        CHANNEL_FROM_SOMEWHERE.writeAndFlush(byteBuf)
                .addListener((ChannelFuture future) -> {
                    // 成功 scala akka？？？？？？？？？？
                    if (future.isSuccess()) {
                        log.info("write successful");
                    } else {
                        log.error("错误", future.cause());
                    }
                });

    }

    public static void writingToChannelWithMutiThreads() {
        final ByteBuf byteBuf = Unpooled.copiedBuffer("some data!".getBytes(StandardCharsets.UTF_8));
        Runnable task = () -> CHANNEL_FROM_SOMEWHERE.write(byteBuf.duplicate());

        // 线程安全的在channel中
        Stream.generate(() -> task)
                .limit(4)
                .forEach(CompletableFuture::runAsync);


    }



}
