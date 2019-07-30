package com.ximo.nettyinaction.sourcestudy.chap02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * @author xikl
 * @date 2019/7/30
 */
@Slf4j
public class Server {

    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            log.info("服务启动成功，端口：{}", port);
        } catch (IOException e) {
            log.info("服务启动失败", e);
        }
    }

    // 异步创建一个线程 来执行
    public void start() {
        CompletableFuture.runAsync(this::doStart).join();
    }

    private void doStart() {
        while (true) {
            final Socket client;
            try {
                // 阻塞住 只有监听到了客户端的连接才会进行操作
                client = serverSocket.accept();
                new ClientHandler(client).start();
            } catch (IOException e) {
                log.info("服务端异常", e);
            }
        }
    }

}
