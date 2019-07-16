package com.ximo.nettyinaction.chapter02;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 简单的实现类，其中的channelRead0有点意思
 * @author xikl
 * @date 2019/7/15
 */
@Slf4j
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 当已连接的时候 进行调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当在到服务器的连接已经建立之后将被调用，发送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty真的牛逼", CharsetUtil.UTF_8));
    }

    /**
     * 当从服务器接收到一条消息时被调用
     * 多条就会被调用多次
     * 由服务器发送的消息可能会被分块接收。也就是说，如果服务器发送了 5 字节，那么不
     * 能保证这 5 字节会被一次性接收。即使是对于这么少量的数据，channelRead0()方法也可能
     * 会被调用两次，第一次使用一个持有 3 字节的 ByteBuf（Netty 的字节容器），第二次使用一个
     * 持有 2 字节的 ByteBuf
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("Client received: {}", msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生错误", cause);
        ctx.close();
    }
}
