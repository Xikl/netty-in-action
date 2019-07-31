package com.ximo.nettyinaction.sourcestudy.chap03;

import com.ximo.nettyinaction.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author xikl
 * @date 2019/7/31
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("channelRegistered");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("handlerAdded");
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

        CompletableFuture.runAsync(() -> doSomeSlowTask(ctx));
    }

    private void doSomeSlowTask(ChannelHandlerContext ctx) {
        String result = loadFromDB();

        ctx.channel().writeAndFlush(result);
        ctx.executor().schedule(this::runScheduledTask, 1, TimeUnit.SECONDS);
    }

    private void runScheduledTask() {
        log.info("定时任务执行");
        CommonUtil.sleep(3);
    }

    private String loadFromDB() {
        return "hello world!";
    }




}
