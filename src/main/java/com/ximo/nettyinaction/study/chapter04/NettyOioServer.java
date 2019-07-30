package com.ximo.nettyinaction.study.chapter04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 采用Netty来重构刚刚的那段代码 阻塞版本
 *
 * @author xikl
 * @date 2019/7/17
 */
public class NettyOioServer {

    public void server(int port) throws InterruptedException {

        //创建 ServerBootstrap 同步阻塞
        OioEventLoopGroup oioEventLoopGroup = new OioEventLoopGroup();
        try {

            ChannelHandler channelHandler = new ChannelInitializer<SocketChannel>() {
                /**
                 * 添加一个 ChannelInboundHandlerAdapter以拦截和处理事件
                 *
                 * @param ch
                 */
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new NettyOioServerHandler());
                }
            };
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(oioEventLoopGroup)
                    // 使用 OioEventLoopGroup以允许阻塞模式（旧的I/O）
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    // 指定 ChannelInitializer，对于每个已接受的连接都调用它
                    .childHandler(channelHandler)
                    // 绑定
                    .bind()
                    // 等待连接上
                    .sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            // 释放group的资源
            oioEventLoopGroup.shutdownGracefully().sync();
        }

    }

    /**
     * 同步的handler 发送一个消息
     *
     */
    private static class NettyOioServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ByteBuf byteBuf = Unpooled.copiedBuffer("Hi!\r\n", StandardCharsets.UTF_8);
            byteBuf = Unpooled.unreleasableBuffer(byteBuf);
            ctx.writeAndFlush(byteBuf.duplicate())
                    // 添加关闭的监听器 以便接收到消息就关闭它
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }





}
