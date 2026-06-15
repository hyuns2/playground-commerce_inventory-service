package io.playground.inventoryservice.infrastructure.kafka.consumer;

import io.playground.inventoryservice.infrastructure.kafka.model.EventEnvelope;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxEntity;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxJpaRepository;
import io.playground.inventoryservice.infrastructure.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final InboxJpaRepository inboxRepository;
    private final JsonUtil jsonUtil;

    @RetryableTopic(
            exclude = DataIntegrityViolationException.class,
            attempts = "3",
            backoff = @Backoff(delay = 5000),
            dltTopicSuffix = ".dlq"
    )
    @KafkaListener(
            topics = "order.events",
            groupId = "inventory-orderExpired"
    )
    public void consumeOrderEvents(String message,
                                   Acknowledgment acknowledgment) {
        EventEnvelope envelope = jsonUtil.fromJson(
                message,
                EventEnvelope.class
        );

        try {
            ConsumedEvent.EventType.valueOf(
                    envelope.getEventType()
            );
        } catch (IllegalArgumentException e) {
            acknowledgment.acknowledge();
            return;
        }

        InboxEntity event = InboxEntity.from(
                envelope
        );

        try {
            inboxRepository.save(
                    event
            );
        } catch (DataIntegrityViolationException e) {
            acknowledgment.acknowledge();
            return;
        }

        acknowledgment.acknowledge();
    }
}
