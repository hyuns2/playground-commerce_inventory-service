package io.playground.inventoryservice.infrastructure.batch.inbox;

import io.playground.inventoryservice.infrastructure.kafka.consumer.ConsumedEvent;
import io.playground.inventoryservice.infrastructure.kafka.consumer.InboxHandler;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboxProcessor implements ItemProcessor<InboxEntity, InboxEntity> {
    private final InboxHandler inboxHandler;

    @Override
    public InboxEntity process(InboxEntity event) {
        if (event.getEventType() ==
                ConsumedEvent.EventType.ORDER_EXPIRED)
            inboxHandler
                    .handleOrderExpired(event);

        if (event.getEventType() ==
                ConsumedEvent.EventType.CANCEL_ALL)
            inboxHandler
                    .handleCancelAll(event);

        if (event.getEventType() ==
                ConsumedEvent.EventType.CANCEL_PARTIALLY)
            inboxHandler
                    .handleCancelPartially(event);

        return event;
    }
}
