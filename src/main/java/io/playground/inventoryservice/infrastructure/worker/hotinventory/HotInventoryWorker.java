package io.playground.inventoryservice.infrastructure.worker.hotinventory;

import io.playground.inventoryservice.application.usecase.HotReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotInventoryWorker {
    private final HotReservationService hotReservationService;
    private final HotInventoryWorkerProperties properties;

    @Scheduled(fixedDelayString = "${tuning.hot-inventory-worker.scheduling-interval}")
    public void processInbox() {
        log.warn("[HotInventoryWorker] Recache stocks for targetIds: {}", properties.getTargetIds());

        hotReservationService.recacheStocks(properties.getTargetIds());
    }
}
