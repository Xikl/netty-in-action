package com.ximo.nettyinaction.chapter02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;

/**
 * @author 朱文赵
 * @date 2019/7/15
 */
@Slf4j
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 创建自己的服务器处理器
        EchoServerHandler echoServerHandler = new EchoServerHandler();
        // 创建EventLoop
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 创建ServerBootstrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = serverBootstrap.group(eventLoopGroup)
                    // 指定Nio的channel
                    .channel(NioServerSocketChannel.class)
                    // 指定特定端口
                    .localAddress(new InetSocketAddress(port))
                    // 添加一个EchoServerHandler到于Channel的 ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    }).bind().sync();
        }finally {
            SocketAddress socketAddress = Optional.ofNullable(channelFuture)
                    .map(ChannelFuture::channel)
                    .map(Channel::localAddress)
                    .orElse(null);
            log.info("{} started and listening for connections on {}", EchoServer.class.getName(), socketAddress);
            eventLoopGroup.shutdownGracefully().sync();
        }

    }


}
