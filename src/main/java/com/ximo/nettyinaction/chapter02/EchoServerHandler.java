package com.ximo.nettyinaction.chapter02;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 你的应用程序应该提供至少有一个实现了
 * exceptionCaught()方法的 ChannelHandler。
 *
 * @author 朱文赵
 * @date 2019/7/15
 */
@Sharable
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 这里应该是客户端传入的就是这个类型
        ByteBuf inByteBuf = (ByteBuf) msg;
        log.info("Server received: {}", inByteBuf.toString(CharsetUtil.UTF_8));
        // 写出数据 异步操作
        ChannelFuture channelFuture = ctx.write(inByteBuf);
    }

    /**
     * 读取完成的时候 调用
     *
     * @param ctx 上下文
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 写出数据 然后关闭改通道
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("数据读取失败", cause);
        // 发生错误直接关闭
        ctx.close();
    }
}
