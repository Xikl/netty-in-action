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

    public static void main(String[] args) throws InterruptedException {
        int port = 8081;
        new EchoServer(port).start();
    }

    public void start() throws InterruptedException {
        // 创建EventLoop
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 创建ServerBootstrap
        ChannelHandler channelHandler = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                // 责任链模式
                ch.pipeline().addLast(new EchoServerHandler());
            }
        };
        try {
            // ServerBootstrap 用于服务端
            // 他的group 则需要两个（也可以是同一个实例）一个parent 一个 child
            // ServerBootstrap 类也可以只使用一个 EventLoopGroup，此时其将在两个场景下共用
            // 同一个 EventLoopGroup
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(eventLoopGroup)
                    // 指定Nio的channel
                    .channel(NioServerSocketChannel.class)
                    // 指定特定端口
                    .localAddress(new InetSocketAddress(port))
                    // 添加一个EchoServerHandler到于Channel的 ChannelPipeline
                    .childHandler(channelHandler)
                    // 绑定
                    .bind()
                    // 同步 直到绑定完成
                    .sync();
            Channel channel = channelFuture.channel();
            log.info("{} started and listening for connections on {}", EchoServer.class.getName(), channel.localAddress());
            channel.closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup，释放所有的资源
            eventLoopGroup.shutdownGracefully().sync();
        }

    }


}
