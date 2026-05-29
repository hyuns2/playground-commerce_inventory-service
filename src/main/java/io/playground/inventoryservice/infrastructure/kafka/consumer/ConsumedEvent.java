package io.playground.inventoryservice.infrastructure.kafka.consumer;

import java.math.BigDecimal;
import java.util.Map;

public class ConsumedEvent {
    public enum EventType {
        ORDER_EXPIRED,
        CANCEL_ALL,
        CANCEL_PARTIALLY
    }

    public record OrderExpired(
            String orderExternalId
    ) {
    }

    public record CancelAll(
            String orderExternalId
    ) {
    }

    public record CancelPartially(
            String idempotencyKey,
            String orderExternalId,
            Map<Long, Integer> canceledVariantQuantities,
            BigDecimal canceledAmount
    ) {
    }
}
