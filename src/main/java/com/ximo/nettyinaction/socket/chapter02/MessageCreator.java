package com.ximo.nettyinaction.socket.chapter02;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author xikl
 * @date 2020/8/12
 */
public class MessageCreator {

    private static final String UNIQUE_ID_HEADER = "收到，我是：";

    public static final String PORT_HEADER = "这是暗号，port：";

    public static String buildWithPort(int port) {
        return PORT_HEADER + port;
    }

    public static int parePort(String data) {
        if (StringUtils.startsWith(data, PORT_HEADER)) {
            final String result = data.substring(PORT_HEADER.length());
            return Integer.parseInt(result);
        }
        return -1;
    }

    public static String buildWithUniqueId(String uniqueId) {
        return UNIQUE_ID_HEADER + uniqueId;
    }

    public static Optional<String> parseUniqueId(String data) {
        if (StringUtils.startsWith(data, UNIQUE_ID_HEADER)) {
            return Optional.of(data.substring(UNIQUE_ID_HEADER.length()));
        }

        return Optional.empty();
    }


}
