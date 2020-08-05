package com.ximo.nettyinaction.socket.chapter02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;

/**
 * @author xikl
 * @date 2020/8/5
 */
@Slf4j
public class UDPSearcher {

    public static void main(String[] args) throws IOException {
        log.info("搜索方");

        // 不需要指定目标地址
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            String data = "hello world";
            DatagramPacket datagramPacket = new DatagramPacket(data.getBytes(), data.length());
            datagramPacket.setAddress(InetAddress.getLocalHost());
            datagramPacket.setPort(20000);

            datagramSocket.send(datagramPacket);

            final byte[] buf = new byte[512];
            DatagramPacket resultPacket = new DatagramPacket(buf, buf.length);
            datagramSocket.receive(resultPacket);

            final byte[] resultDataByteArray = resultPacket.getData();
            String resultData = new String(resultDataByteArray, 0, resultDataByteArray.length);
            final int sourcePort = resultPacket.getPort();
            final String sourcePostAddress = resultPacket.getAddress().getHostAddress();
            log.info("port: {}, host: {}, data:{}", sourcePort, sourcePostAddress, resultData);

            log.info("end");
        }




    }

}
