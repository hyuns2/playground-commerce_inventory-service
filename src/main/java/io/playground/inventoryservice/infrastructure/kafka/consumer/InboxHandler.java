package io.playground.inventoryservice.infrastructure.kafka.consumer;

import io.playground.inventoryservice.application.usecase.ReservationService;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxEntity;
import io.playground.inventoryservice.infrastructure.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboxHandler {
    private final JsonUtil jsonUtil;
    private final ReservationService reservationService;

    public void handleOrderExpired(InboxEntity event) {
        reservationService.reStocks(
                jsonUtil.fromJson(
                        event.getPayload(),
                        ConsumedEvent.OrderExpired.class
                ).orderExternalId()
        );
    }

    public void handleCancelAll(InboxEntity event) {
        reservationService.restoreAllStocks(
                jsonUtil.fromJson(
                        event.getPayload(),
                        ConsumedEvent.CancelAll.class
                ).orderExternalId()
        );
    }

    public void handleCancelPartially(InboxEntity event) {
        ConsumedEvent.CancelPartially payload =
                jsonUtil.fromJson(
                        event.getPayload(),
                        ConsumedEvent.CancelPartially.class
                );

        reservationService.restorePartialStocks(
                payload.idempotencyKey(),
                payload.orderExternalId(),
                payload.canceledVariantQuantities()
        );
    }
}
