package io.playground.inventoryservice.infrastructure.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope {
    private String eventId;
    private String eventType;
    private String occurredAt;
    private String traceId;
    private String payload;

    public static EventEnvelope of(String eventId,
                                   String eventType,
                                   Instant occurredAt,
                                   String traceId,
                                   String payload) {
        return new EventEnvelope(
                eventId,
                eventType,
                occurredAt.toString(),
                traceId,
                payload
        );
    }
}
