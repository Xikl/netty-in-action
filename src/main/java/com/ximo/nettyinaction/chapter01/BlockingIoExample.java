package com.ximo.nettyinaction.chapter01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 传统io的网络请求操作
 * 阻塞，会开启大量线程，且会出现线程长时间不工作都在等待的过程
 *
 * @author xikl
 * @date 2019/7/15
 */
public class BlockingIoExample {

    public void serve(int portNumber) throws IOException {
        // 创建一个新的ServerSocket用来监听指定端口上的连接请求
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // 建立一个连接
        Socket clientSocket = serverSocket.accept();
        // 读取
        BufferedReader inBufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // 写出 response
        PrintWriter outPrintWriter = new PrintWriter(clientSocket.getOutputStream(), true);

        String request, response;

        while ((request = inBufferedReader.readLine()) != null) {
            if ("Done".equals(request)) {
                break;
            }
            response = processRequest(request);
            // response写出 可以理解为 json的返回
            outPrintWriter.println(response);
        }

    }

    private String processRequest(String request) {
        return "Processed";
    }


}
