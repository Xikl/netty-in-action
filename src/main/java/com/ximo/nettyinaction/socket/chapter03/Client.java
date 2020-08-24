package com.ximo.nettyinaction.socket.chapter03;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @author xikl
 * @date 2020/8/19
 */
public class Client {
    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();

        initSocket(socket);

        // 链接到本地20000端口，超时时间3秒，超过则抛出超时异常
        // 特别注意只要socket中带上了服务器的信息那么就会连接
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);

        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            // 发送接收数据
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        // 释放资源，可以try resources 进行关闭，或者加final
        socket.close();
        System.out.println("客户端已退出～");

    }

    /**
     * 学习各种的构造函数
     * 使用Ipv4获得本地端口
     *
     * @return 客户端socket，推荐使用无参构造，然后设置各种属性
     * @throws IOException io异常
     */
    private static Socket createSocket() throws IOException {
        // 无代理模式，等效于空构造函数
//        Socket socket = new Socket(Proxy.NO_PROXY);

        // 新建一份具有HTTP代理的套接字，传输数据将通过www.baidu.com:8080端口转发
//        Proxy proxy = new Proxy(Proxy.Type.HTTP,
//                new InetSocketAddress(Inet4Address.getByName("www.baidu.com"), 8800));
//        Socket sockt = new Socket(proxy);

        // 新建一个套接字，并且直接链接到本地20000的服务器上
//        Socket socket = new Socket("localhost", PORT);

        // 等同于 localhost
//        final InetAddress localHost = InetAddress.getLocalHost();
        // 新建一个套接字，并且直接链接到本地20000的服务器上
//        Socket socket = new Socket(localHost, PORT);


        // 新建一个套接字，并且直接链接到本地20000的服务器上，并且绑定到本地20001端口上
        // 由于都是连接到本地 所以需要连接的服务器端host和本地的保持一致
//        Socket socket = new Socket("localhost", PORT, Inet4Address.getLocalHost(), LOCAL_PORT);
//        Socket socket = new Socket(Inet4Address.getLocalHost(), PORT, Inet4Address.getLocalHost(), LOCAL_PORT);

        // bind 方法
        Socket socket = new Socket();
        // 绑定到本地20001端口
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));

        return socket;
    }

    /**
     * 配置socket的属性信息
     *
     * @param socket 客户端socket
     * @throws SocketException 异常信息
     */
    private static void initSocket(Socket socket) throws SocketException {
        // 设置读取超时时间为2秒
        socket.setSoTimeout(2000);

        // 是否复用未完全关闭的Socket地址，对于指定bind操作后的套接字有效
        socket.setReuseAddress(true);

        // 是否开启Nagle算法，优化网络空间，减少脏数据产生，保证性能
        socket.setTcpNoDelay(true);

        // 是否需要在长时无数据响应时发送确认数据（类似心跳包），时间大约为2小时
        socket.setKeepAlive(true);

        // 对于close关闭操作行为进行怎样的处理；默认为false，0
        // false、0：默认情况，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
        // true、0：关闭时立即返回，缓冲区数据抛弃，直接发送RST结束命令到对方，并无需经过2MSL等待
        // true、200：关闭时最长阻塞200毫秒，随后按第二情况处理
        socket.setSoLinger(true, 20);

        // 是否让紧急数据内敛，默认false；紧急数据通过 socket.sendUrgentData(1);发送
        // 不建议设置 会和正常的数据混淆
        socket.setOOBInline(true);

        // 设置接收发送缓冲器大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);

        // 设置性能参数：短链接，延迟，带宽的相对重要性
        // connection 短连接 连接一会就断开
        // latency 延迟 要低 值就越低
        // bandWith 带宽  组合发送
        socket.setPerformancePreferences(1, 1, 0);
    }

    private static void todo(Socket client) throws IOException {
        // 得到Socket输出流
        OutputStream outputStream = client.getOutputStream();


        // 得到Socket输入流
        InputStream inputStream = client.getInputStream();
        byte[] buffer = new byte[256];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        // byte
        byteBuffer.put((byte) 126);

        // char
        char c = 'a';
        byteBuffer.putChar(c);

        // int
        int i = 2323123;
        byteBuffer.putInt(i);

        // bool
        boolean b = true;
        byteBuffer.put(b ? (byte) 1 : (byte) 0);

        // Long
        long l = 298789739;
        byteBuffer.putLong(l);


        // float
        float f = 12.345f;
        byteBuffer.putFloat(f);


        // double
        double d = 13.31241248782973;
        byteBuffer.putDouble(d);

        // String
        String str = "Hello你好！";
        byteBuffer.put(str.getBytes());

        // 发送到服务器
        outputStream.write(buffer, 0, byteBuffer.position() + 1);

        // 接收服务器返回
        int read = inputStream.read(buffer);
        System.out.println("收到数量：" + read);

        // 资源释放
        outputStream.close();
        inputStream.close();
    }
}
