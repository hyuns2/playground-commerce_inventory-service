package io.playground.inventoryservice.infrastructure.worker.config;

import io.playground.inventoryservice.infrastructure.worker.inbox.InboxWorkerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@RequiredArgsConstructor
public class WorkerConfig {
    private final InboxWorkerProperties inboxWorkerProperties;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(@Value("${tuning.scheduler.pool-size}")
                                                     int poolSize) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setThreadNamePrefix("common-scheduler-");
        scheduler.setPoolSize(poolSize);

        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public ThreadPoolTaskExecutor inboxWorkerPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setThreadNamePrefix("inbox-worker-");
        executor.setCorePoolSize(inboxWorkerProperties.getPoolSize());
        executor.setMaxPoolSize(inboxWorkerProperties.getPoolSize());

        // 작업이 몰릴 때 큐에 쌓지 않고 바로 실행하도록 설정 (Backpressure)
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(
                new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.initialize();
        return executor;
    }
}
