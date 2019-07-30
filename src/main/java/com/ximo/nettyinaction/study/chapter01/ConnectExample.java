package com.ximo.nettyinaction.study.chapter01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * 异步建立连接和java8的{@link CompletableFuture}的异同
 *
 * @author xikl
 * @date 2019/7/15
 */
@Slf4j
public class ConnectExample {

    public static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /**
     *
     * @see ChannelFuture 管道Future
     * @see java.util.concurrent.Future
     */
    public static void connect() {
        ChannelFuture channelFuture =
                CHANNEL_FROM_SOMEWHERE.connect(new InetSocketAddress("192.168.0.1", 25));
        // 异步回调？ 利用监听器的机制
        // 注意这里是一个ChannelFuture
        channelFuture.addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
                future.channel().writeAndFlush(buffer);
                // do something
            } else {
                log.error("数据读取失败", future.cause());
            }
        });

    }
}
