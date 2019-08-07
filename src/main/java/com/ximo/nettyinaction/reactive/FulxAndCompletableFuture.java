package com.ximo.nettyinaction.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 朱文赵
 * @date 2019/7/30
 */
@Slf4j
public class FulxAndCompletableFuture {

    public static void byCompletableFuture() {

        CompletableFuture<List<String>> ids = ifhIds();

        CompletableFuture<List<String>> result = ids.thenComposeAsync(l -> {
            Stream<CompletableFuture<String>> zip =
                    l.stream().map(i -> {
                        CompletableFuture<String> nameTask = ifhName(i);
                        CompletableFuture<Integer> statTask = ifhStat(i);

                        return nameTask.thenCombineAsync(statTask, (name, stat) -> "Name " + name + " has stats " + stat);
                    });
            List<CompletableFuture<String>> combinationList = zip.collect(Collectors.toList());
            CompletableFuture<String>[] combinationArray = combinationList.toArray(new CompletableFuture[combinationList.size()]);

            CompletableFuture<Void> allDone = CompletableFuture.allOf(combinationArray);
            return allDone.thenApply(v -> combinationList.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
        });

        List<String> results = result.join();
        results.forEach(System.out::println);
    }

    private static CompletableFuture<List<String>> ifhIds() {
        log.info("ifhIds");
        CompletableFuture<List<String>> ids = new CompletableFuture<>();
        ids.complete(Arrays.asList("Joe", "Bart", "Henry", "Nicole", "Jack"));
        return ids;
    }

    private static CompletableFuture<String> ifhName(String id) {
        sleep(3);
        log.info("ifhName");
        CompletableFuture<String> f = new CompletableFuture<>();
        f.complete("Name" + id);
        return f;
    }

    private static CompletableFuture<Integer> ifhStat(String id) {
        log.info("ifhStat");
        sleep(2);
        CompletableFuture<Integer> f = new CompletableFuture<>();
        f.complete(id.length() + 100);
        return f;
    }

    private static void sleep(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        byCompletableFuture();
        Thread.currentThread().join();
    }

}
