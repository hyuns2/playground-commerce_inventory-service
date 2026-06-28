package io.playground.inventoryservice.infrastructure.worker.hotinventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "tuning.hot-inventory-worker")
@Getter
@AllArgsConstructor
public class HotInventoryWorkerProperties {
    private final String cacheName;
    private final long schedulingInterval;
    private final List<Long> targetIds;
    private final double factor;
}
