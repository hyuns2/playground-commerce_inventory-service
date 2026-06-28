package io.playground.inventoryservice.infrastructure.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tuning.inbox-worker")
@Getter
@AllArgsConstructor
public class InboxWorkerProperties {
    private final long schedulingInterval;
    private final int poolSize;
    private final int limitSize;
    private final int retryMax;
}
