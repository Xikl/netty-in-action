package com.ximo.nettyinaction.socket.chapter01;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * @author xikl
 * @date 2020/8/4
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2000);
        log.info("服务器开始响应");
        log.info("服务器信息，host: {}, port: {}", serverSocket.getInetAddress(), serverSocket.getLocalPort());

        while (true) {
            // 会block住
            final Socket client = serverSocket.accept();
            final ClientHandler clientHandler = new ClientHandler(client);
            CompletableFuture.runAsync(clientHandler);
        }

    }

}
