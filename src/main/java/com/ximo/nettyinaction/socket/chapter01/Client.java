package com.ximo.nettyinaction.socket.chapter01;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.*;

/**
 * @author xikl
 * @date 2020/8/4
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws IOException {
        // 自动关闭
        try(Socket socket = new Socket()){
            // 设置socket超时时间
            socket.setSoTimeout(3000);
            final InetSocketAddress endpoint = new InetSocketAddress(InetAddress.getLocalHost(), 2000);
            // 超时时间为3000ms
            socket.connect(endpoint, 3000);
            log.info("连接成功");
            log.info("客户端信息，host: {}， port: {}", socket.getLocalAddress(), socket.getLocalPort());
            // socket.getInetAddress()
            // Returns the address to which the socket is connected.

            // socket.getPort()
            // 获得远端连接的端口号
            log.info("服务端端信息，host: {}， port: {}", socket.getInetAddress(), socket.getPort());
            sendMessage(socket);
        }
        log.info("客户端已经退出");

    }

    private static void sendMessage(Socket socket) throws IOException {
        try (InputStream input = System.in;
             final BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(input));

             // socket 的输出流
             final OutputStream outputStream = socket.getOutputStream();
             final PrintStream socketPrintStream = new PrintStream(outputStream);

             // socket 输入流，服务器的返回
             final InputStream inputStream = socket.getInputStream();
             final BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            while (true) {
                // 获得键盘输入打印出来
                final String line = inputBufferedReader.readLine();
                // 发送给服务器
                socketPrintStream.println(line);

                // 获得服务器的响应结果
                final String result = socketBufferedReader.readLine();
                log.info("响应结果：{}", result);
                if (StringUtils.equalsIgnoreCase(result, "exit")) {
                    break;
                }
            }
        }




    }


}
