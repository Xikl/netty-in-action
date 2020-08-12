package com.ximo.nettyinaction.socket.chapter02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * @author xikl
 * @date 2020/8/5
 */
@Slf4j
public class UDPSearcher {

    private static final int LISTEN_PORT = 30000;

    public static void main(String[] args) throws InterruptedException, IOException {

        final Listener listener = listen();
        sendBroadcast();

        final int result = System.in.read();
        final List<Device> devicesList = listener.getDevicesAndClose();
        devicesList.forEach(System.out::println);
    }

    private static Listener listen() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();

        countDownLatch.await();
        return listener;
    }

    private static void sendBroadcast() {
        log.info("搜索方开始");
        // 不需要指定目标地址
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            String data = MessageCreator.buildWithPort(LISTEN_PORT);

            // 注意这里应该是bytes的长度
            final byte[] bytes = data.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
            // 本地广播地址
            datagramPacket.setAddress(InetAddress.getByName("255.255.255.255"));
            datagramPacket.setPort(20000);

            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            log.error("异常", e);
        }
    }

    private static class Device {
        private int port;

        private String ip;

        private String uniqueId;

        public Device(int port, String ip, String uniqueId) {
            this.port = port;
            this.ip = ip;
            this.uniqueId = uniqueId;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", uniqueId='" + uniqueId + '\'' +
                    '}';
        }
    }

    private static class Listener extends Thread {
        private final int listenPort;

        private final CountDownLatch countDownLatch;

        private boolean done = false;

        private DatagramSocket ds = null;

        private final List<Device> devices = new ArrayList<>();

        public Listener(int listenPort, CountDownLatch countDownLatch) {
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();
            countDownLatch.countDown();
            try {
                ds = new DatagramSocket(listenPort);
                while (!done) {
                    // 进行消息的接收
                    final byte[] buf = new byte[512];
                    DatagramPacket resultPacket = new DatagramPacket(buf, buf.length);
                    ds.receive(resultPacket);

                    final String hostAddress = resultPacket.getAddress().getHostAddress();
                    final int port = resultPacket.getPort();
                    final byte[] data = resultPacket.getData();
                    String resultData = new String(data);
                    log.info("port: {}, host: {}, data:{}", port, hostAddress, resultData);

                    Optional<String> uniqueIdOptional = MessageCreator.parseUniqueId(resultData);
                    if (uniqueIdOptional.isPresent()) {
                        final String uniqueId = uniqueIdOptional.get();
                        Device device = new Device(port, hostAddress, uniqueId);
                        devices.add(device);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
            log.info("end");
        }

        private List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }
    }
}
