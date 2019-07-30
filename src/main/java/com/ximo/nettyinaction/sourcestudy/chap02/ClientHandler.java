package com.ximo.nettyinaction.sourcestudy.chap02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * @author xikl
 * @date 2019/7/30
 */
@Slf4j
public class ClientHandler {

    public static final int MAX_DATA_LEN = 1024;

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        log.info("新的客户端接入");
        CompletableFuture.runAsync(this::doStart);
    }

    private void doStart() {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] data;
            int len;
            while (true) {
                data = new byte[MAX_DATA_LEN];
                while ((len = inputStream.read(data)) != -1) {
                    String message = new String(data, 0, len);
                    log.info("客户端传来消息: {}", message);
                    socket.getOutputStream().write(data);
                }

            }
        } catch (IOException e) {
            log.error("服务端接受消息发生异常", e);
        }

    }
}
