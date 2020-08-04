package com.ximo.nettyinaction.socket.chapter01;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;

/**
 * @author xikl
 * @date 2020/8/4
 */
@Slf4j
public class ClientHandler implements Runnable {

    private Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        log.info("客户端连接， host: {}, port:{}", client.getInetAddress(), client.getPort());
        try (final OutputStream outputStream = client.getOutputStream();
             final PrintStream socketPrintStream = new PrintStream(outputStream);

             final InputStream inputStream = client.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            while (true) {
                final String result = bufferedReader.readLine();
                log.info("服务端响应结果：{}", result);
                if (StringUtils.equalsIgnoreCase(result, "exit")) {
                    socketPrintStream.println("exit");
                    break;
                }
                socketPrintStream.println("响应：" + result.length());
            }
        } catch (IOException e) {
            log.error("服务器异常", e);
        }finally {
            try {
                client.close();
                log.info("客户端已经退出，host: {}, port: {}", client.getInetAddress(), client.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

