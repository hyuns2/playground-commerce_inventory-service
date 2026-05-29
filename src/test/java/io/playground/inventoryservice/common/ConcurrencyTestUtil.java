package io.playground.inventoryservice.common;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ConcurrencyTestUtil {
    @Builder
    public record ConcurrencyResult(
            int successCount,
            int failureCount,
            List<Throwable> errors
    ) {
    }

    public static ConcurrencyResult runConcurrently(Runnable task,
                                                    int threadCount) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Throwable> errors = Collections.synchronizedList(
                new ArrayList<>()
        );

        for (int i = 0; i < threadCount; i++)
            executorService.submit(() -> {
                executorService.submit(() -> {
                    log.info("스레드 {} 시작 대기", Thread.currentThread().getName());

                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        endLatch.countDown();
                        return;  // 시작 못 한 케이스는 집계 안 함
                    }

                    log.info("스레드 {} 작업 시작", Thread.currentThread().getName());

                    try {
                        task.run();
                        successCount.incrementAndGet();
                    } catch (Throwable e) {
                        failureCount.incrementAndGet();
                        errors.add(e);
                    } finally {
                        endLatch.countDown();
                    }

                    log.info("스레드 {} 작업 완료", Thread.currentThread().getName());
                });
            });

        startLatch.countDown();

        boolean finished = endLatch.await(
                30, TimeUnit.SECONDS
        );
        executorService.shutdown();

        if (!finished) {
            executorService.shutdownNow();
            throw new IllegalStateException("Concurrency test timed out after 30 seconds");
        }

        return ConcurrencyResult.builder()
                .successCount(successCount.get())
                .failureCount(failureCount.get())
                .errors(errors)
                .build();
    }
}
