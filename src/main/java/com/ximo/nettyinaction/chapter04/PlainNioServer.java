package com.ximo.nettyinaction.chapter04;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * 第一次写Nio socket编程 实在是太难了吧
 *
 * @author xikl
 * @date 2019/7/17
 */
@Slf4j
public class PlainNioServer {

    public void serve(int port) throws IOException {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.configureBlocking(false);
            try (ServerSocket ss = serverChannel.socket()) {
                InetSocketAddress address = new InetSocketAddress(port);
                //将服务器绑定到选定的端口
                ss.bind(address);
                //打开Selector来处理 Channel
                try (Selector selector = Selector.open()) {
                    //将ServerSocketChannel注册到Selector以接受连接
                    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                    final ByteBuffer msg = ByteBuffer.wrap("Hello!\r\n".getBytes(StandardCharsets.UTF_8));
                    for (;;){
                        try {
                            //等待需要处理的新事件；阻塞将一直持续到下一个传入事件
                            selector.select();
                        } catch (IOException ex) {
                            log.error("", ex);
                            //handle exception
                            break;
                        }
                        //获取所有接收事件的SelectionKey实例
                        Set<SelectionKey> readyKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = readyKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            try {
                                //检查事件是否是一个新的已经就绪可以被接受的连接
                                if (key.isAcceptable()) {
                                    try (ServerSocketChannel server = (ServerSocketChannel) key.channel()) {
                                        SocketChannel client = server.accept();
                                        client.configureBlocking(false);
                                        //接受客户端，并将它注册到选择器
                                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                                        log.info("Accepted connection from {}", client);
                                    }
                                }
                                //检查套接字是否已经准备好写数据
                                if (key.isWritable()) {
                                    try (SocketChannel client = (SocketChannel) key.channel()) {
                                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                                        while (buffer.hasRemaining()) {
                                            //将数据写到已连接的客户端
                                            if (client.write(buffer) == 0) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (IOException ex) {
                                key.cancel();
                                log.error("", ex);
                            }
                        }
                    }
                }
            }
        }
    }
}
