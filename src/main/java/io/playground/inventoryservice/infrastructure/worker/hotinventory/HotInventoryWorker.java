package io.playground.inventoryservice.infrastructure.worker.hotinventory;

import io.playground.inventoryservice.application.usecase.HotReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotInventoryWorker {
    private final HotReservationService hotReservationService;
    private final HotInventoryWorkerProperties properties;

    @Scheduled(fixedDelayString = "${tuning.hot-inventory-worker.scheduling-interval}")
    public void processInbox() {
        hotReservationService.recacheStocks(properties.getTargetIds());
    }
}
