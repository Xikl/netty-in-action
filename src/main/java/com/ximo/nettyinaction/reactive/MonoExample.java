package com.ximo.nettyinaction.reactive;

import reactor.core.publisher.Mono;

/**
 * @author 朱文赵
 * @date 2019/7/30
 */
public class MonoExample {

    public static void main(String[] args) {
        monoJust();
    }

    private static void monoJust() {
        Mono.just("name")
                .map(String::toUpperCase)
                .map(String::length)
                .subscribe(System.out::println);
    }


}
