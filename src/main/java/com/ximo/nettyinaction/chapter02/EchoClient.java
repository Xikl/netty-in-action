package com.ximo.nettyinaction.chapter02;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author xikl
 * @date 2019/7/15
 */
public class EchoClient {

    private String host;
    private int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try{
            // 责任链模式
            ChannelHandler channelHandler = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new EchoClientHandler());
                }
            };
            ChannelFuture channelFuture = new Bootstrap()
                    //指定 EventLoopGroup 以处理客户端事件；需要适用于 NIO 的实现
                    .group(eventLoopGroup)
                    //适用于 NIO 传输的Channel 类型
                    .channel(NioSocketChannel.class)
                    // 设置服务器的InetSocketAddress
                    .remoteAddress(new InetSocketAddress(host, port))
                    // 添加处理器
                    .handler(channelHandler)
                    // 连接到远程节点
                    .connect()
                    // 阻塞等待连接完成
                    .sync();

            // 阻塞关闭
            channelFuture.channel().closeFuture().sync();
        }finally {
            eventLoopGroup.shutdownGracefully().sync();
        }


    }

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 8081;
        new EchoClient(host, port).start();
    }
}
