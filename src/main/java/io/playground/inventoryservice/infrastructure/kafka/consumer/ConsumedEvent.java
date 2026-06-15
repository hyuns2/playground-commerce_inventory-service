package io.playground.inventoryservice.infrastructure.kafka.consumer;

import java.math.BigDecimal;
import java.util.Map;

public class ConsumedEvent {
    public enum EventType {
        ORDER_EXPIRED,
        ALL_CANCELED,
        PARTIALLY_CANCELED
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
