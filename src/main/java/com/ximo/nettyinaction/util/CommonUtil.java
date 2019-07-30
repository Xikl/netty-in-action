package com.ximo.nettyinaction.util;

import java.util.concurrent.TimeUnit;

/**
 * @author xikl
 * @date 2019/7/30
 */
public class CommonUtil {


    private CommonUtil() {
        throw new UnsupportedOperationException();
    }

    public static void sleep(long timeoutSeconds) {
        try {
            TimeUnit.SECONDS.sleep(timeoutSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
