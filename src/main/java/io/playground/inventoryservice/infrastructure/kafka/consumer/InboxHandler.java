package io.playground.inventoryservice.infrastructure.kafka.consumer;

import io.playground.inventoryservice.application.usecase.ReservationService;
import io.playground.inventoryservice.infrastructure.kafka.model.EventEnvelope;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxEntity;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxPersistenceAdapter;
import io.playground.inventoryservice.infrastructure.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InboxHandler {
    private final InboxPersistenceAdapter inboxPersistence;
    private final ReservationService reservationService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonUtil jsonUtil;

    @Transactional
    public List<InboxEntity> findNotProcessedInboxes(int limitSize,
                                                     int retryMax) {
        List<InboxEntity> outboxEntities = inboxPersistence
                .findNotProcessedInboxes(limitSize, retryMax);

        for (InboxEntity entity : outboxEntities)
            entity.updateLockedUntil(
                    Instant.now()
                            .plus(Duration.ofMinutes(1))
            );

        return outboxEntities;
    }

    @Transactional
    public void markProcessed(InboxEntity entity) {
        inboxPersistence.updateProcessedById(
                entity.getId(),
                true
        );
    }

    @Transactional
    public void markRetryOrFail(InboxEntity entity,
                                int retryMax) {
        if (entity.getRetryCount() < retryMax) {
            inboxPersistence.updateRetryCountAndLockedUntil(
                    entity.getId(),
                    null
            );

            return;
        }

        try {
            kafkaTemplate.send(
                    "inventory.events",
                    entity.getEventId(),
                    jsonUtil.toJson(
                            EventEnvelope.of(
                                    entity.getEventId(),
                                    "INBOXING_FAILED",
                                    entity.getOccurredAt(),
                                    entity.getTraceId(),
                                    entity.getId().toString()
                            )
                    )
            ).get();
        } catch (Exception e) {
            log.error("Failed to send INBOXING_FAILED event for inbox id: {}, event id: {}",
                    entity.getId(), entity.getEventId());
        }
    }

    public void handle(InboxEntity entity) {
        switch (entity.getEventType()) {
            case ORDER_EXPIRED -> handleOrderExpired(entity);
            case ALL_CANCELED -> handleCancelAll(entity);
            case PARTIALLY_CANCELED -> handleCancelPartially(entity);
        }
    }

    private void handleOrderExpired(InboxEntity entity) {
        reservationService.reStocks(
                jsonUtil.fromJson(
                        entity.getPayload(),
                        ConsumedEvent.OrderExpired.class
                ).orderExternalId()
        );
    }

    private void handleCancelAll(InboxEntity entity) {
        reservationService.restoreAllStocks(
                jsonUtil.fromJson(
                        entity.getPayload(),
                        ConsumedEvent.CancelAll.class
                ).orderExternalId()
        );
    }

    private void handleCancelPartially(InboxEntity entity) {
        ConsumedEvent.CancelPartially payload =
                jsonUtil.fromJson(
                        entity.getPayload(),
                        ConsumedEvent.CancelPartially.class
                );

        reservationService.restorePartialStocks(
                payload.idempotencyKey(),
                payload.orderExternalId(),
                payload.canceledVariantQuantities()
        );
    }
}
