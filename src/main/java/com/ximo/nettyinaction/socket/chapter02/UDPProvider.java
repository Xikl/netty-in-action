package com.ximo.nettyinaction.socket.chapter02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author xikl
 * @date 2020/8/5
 */
@Slf4j
public class UDPProvider {

    public static void main(String[] args) throws IOException {
        log.info("UDPProvider start");
        try (DatagramSocket datagramSocket = new DatagramSocket(20000)) {

            final byte[] buf = new byte[512];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            datagramSocket.receive(receivePacket);

            final String hostAddress = receivePacket.getAddress().getHostAddress();
            final int dataLength = receivePacket.getLength();
            final int port = receivePacket.getPort();
            final byte[] resultDataByteArray = receivePacket.getData();
            final String resultData = new String(resultDataByteArray, 0, dataLength);

            log.info("UDPProvider receive from: port: {}, host:{}, resultData:{}", port, hostAddress, resultData);

            String feedBackData = "receive data with length: " + dataLength;
            // 发送回给源服务器
            DatagramPacket feedBackPacket = new DatagramPacket(
                    feedBackData.getBytes(),
                    feedBackData.length(),
                    receivePacket.getAddress(),
                    receivePacket.getPort()
            );

            datagramSocket.send(feedBackPacket);
            log.info("finished");
        }


    }

}
