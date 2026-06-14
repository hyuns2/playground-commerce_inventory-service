package io.playground.inventoryservice.infrastructure.persistence.eventstream;

import io.playground.inventoryservice.infrastructure.kafka.consumer.ConsumedEvent;
import io.playground.inventoryservice.infrastructure.kafka.model.EventEnvelope;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "inboxes"
//        indexes = {
//                @Index(name = "idx_processed", columnList = "processed")
//        }
)
public class InboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsumedEvent.EventType eventType;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private String traceId;

    @Column(nullable = false)
    @Lob
    private String payload;

    @Column(nullable = false)
    private boolean processed;

    @Column(nullable = false)
    private int retryCount;

    @Column
    private Instant lockedUntil;

    public static InboxEntity from(EventEnvelope eventEnvelope) {
        return InboxEntity.builder()
                .eventId(eventEnvelope.getEventId())
                .eventType(ConsumedEvent.EventType.valueOf(eventEnvelope.getEventType()))
                .occurredAt(Instant.parse(eventEnvelope.getOccurredAt()))
                .traceId(eventEnvelope.getTraceId())
                .payload(eventEnvelope.getPayload())
                .processed(false)
                .retryCount(0)
                .lockedUntil(null)
                .build();
    }

    public void updateLockedUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}
