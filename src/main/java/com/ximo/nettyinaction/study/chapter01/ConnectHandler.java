package com.ximo.nettyinaction.study.chapter01;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xikl
 * @date 2019/7/15
 */
@Slf4j
public class ConnectHandler extends ChannelInboundHandlerAdapter {


    /**
     * 当一个新的连接已经被建立时，channelActive(ChannelHandlerContext)将会被调用
     *
     * @param ctx 上下文 类似于jdbc中的connection，里面放有很多东西
     * @see java.sql.Connection
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client {} connected", ctx.channel().remoteAddress());
    }
}
