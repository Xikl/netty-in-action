package com.ximo.nettyinaction.sourcestudy.chap02;

import com.ximo.nettyinaction.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * @author xikl
 * @date 2019/7/30
 */
@Slf4j
public class Client {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) throws IOException {
        final Socket socket = new Socket(HOST, PORT);
        CompletableFuture
                .runAsync(() -> sendMessageToServer(socket))
                .join();

    }

    private static void sendMessageToServer(Socket socket) {
        log.info("客户端启动成功");
        while (true) {
            String message = "hello netty";
            log.info("客户端发送数据：{}", message);
            try {
                socket.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                log.info("客户端发送消息失败", e);
            }
            // 休息5秒
            CommonUtil.sleep(5);
        }
    }

}
