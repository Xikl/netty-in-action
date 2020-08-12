package com.ximo.nettyinaction.socket.chapter02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * @author xikl
 * @date 2020/8/5
 */
@Slf4j
public class UDPProvider {

    public static void main(String[] args) throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        Provider provider = new Provider(uniqueId);

        provider.start();

        // 输入任意字符 退出
        final int result = System.in.read();

        provider.exit();
    }

    private static class Provider extends Thread {

        private final String uniqueId;

        private volatile boolean done = false;

        private DatagramSocket ds = null;

        public Provider(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        @Override
        public void run() {
            super.run();

            log.info("UDPProvider start");
            try {
                // 构建一个监听
                ds = new DatagramSocket(20000);
                while (!done) {

                    // 构建一个监听实体类
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    // 这里拿不到数据将会阻塞在这里
                    ds.receive(receivePacket);

                    final String hostAddress = receivePacket.getAddress().getHostAddress();
                    // 这里一定要拿数据的长度，否则会出现很多无效的字符长度
                    final int dataLength = receivePacket.getLength();
                    final int port = receivePacket.getPort();
                    final byte[] resultDataByteArray = receivePacket.getData();
                    final String resultData = new String(resultDataByteArray, 0, dataLength);

                    log.info("UDPProvider receive from: port: {}, host:{}, resultData:{}", port, hostAddress, resultData);


                    // 根据一定的规则来解析端口号
                    int feedBackPort = MessageCreator.parePort(resultData);
                    if (feedBackPort != -1) {
                        String feedBackData = MessageCreator.buildWithUniqueId(uniqueId);
                        // 发送回给源服务器
                        DatagramPacket feedBackPacket = new DatagramPacket(
                                feedBackData.getBytes(),
                                feedBackData.length(),
                                receivePacket.getAddress(),
                                feedBackPort
                        );
                        ds.send(feedBackPacket);
                    }


                    log.info("finished");

                }
            } catch (IOException e) {
                log.error("异常", e);
            } finally {
                close();
            }


        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        void exit() {
            done = true;
            close();
        }


    }

}
