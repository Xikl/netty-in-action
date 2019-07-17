package com.ximo.nettyinaction.chapter04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 非阻塞的IO
 *
 * 在Netty中，有两种发送消息的方式。你可以直接写到Channel中，也可以 写到和Channel-
 * Handler相关联的ChannelHandlerContext对象中。前一种方式将会导致消息从Channel-
 * Pipeline 的尾端开始流动，而后者将导致消息从 ChannelPipeline 中的下一个 Channel-
 * Handler 开始流动。
 *
 * 个人理解就是 直接加到ServerBoostrap 和 加入到ChannelInitializer中的区别
 *
 * @author xikl
 * @date 2019/7/17
 */
public class NettyNioServer {

    public void server(int port) throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        ChannelHandler channelHandler = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new NettyNioServerHandler());
            }
        };

        try{

            final ChannelFuture channelFuture = new ServerBootstrap()
                    .group(nioEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(channelHandler)
                    .bind()
                    .sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            nioEventLoopGroup.shutdownGracefully().sync();
        }

    }

    private static class NettyNioServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello!\r\n", StandardCharsets.UTF_8);
            byteBuf = Unpooled.unreleasableBuffer(byteBuf);
            ctx.writeAndFlush(byteBuf)
                    .addListener(ChannelFutureListener.CLOSE);

        }
    }



}
