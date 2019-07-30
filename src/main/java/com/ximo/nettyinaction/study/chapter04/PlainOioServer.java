package com.ximo.nettyinaction.study.chapter04;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * 普通的连接socket写法同步io 因为他一直都在死循环
 *
 * @author xikl
 * @date 2019/7/16
 */
@Slf4j
public class PlainOioServer {


    /**
     * java8 重写连接部分
     *
     * @param port
     * @throws IOException
     */
    public void serve(int port) throws IOException {
        //将服务器绑定到指定端口
        // 接受连接
        try(ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept()) {
           while(true){
                log.info("Accepted connection from {} ", clientSocket);
                CompletableFuture.runAsync(() -> {
                    // 将消息写个已经连接的客户端
                    try (OutputStream outputStream = clientSocket.getOutputStream()) {

                        outputStream.write("Hello".getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    } catch (IOException e) {
                        log.error("写出失败", e);
                    }
                });
            }
        } catch (IOException e) {
            log.info("创建socket失败", e);
        }

    }
}
