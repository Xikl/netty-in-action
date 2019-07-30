package com.ximo.nettyinaction.sourcestudy.chap02;

/**
 * @author xikl
 * @date 2019/7/30
 */
public class ServerBoot {

    /** 服务端监听端口 */
    private static final int PORT = 8000;

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.start();
    }
}
