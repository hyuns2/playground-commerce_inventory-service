package io.playground.inventoryservice.infrastructure.worker.inbox;

import io.playground.inventoryservice.infrastructure.kafka.consumer.InboxHandler;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InboxWorker {
    private final InboxHandler inboxHandler;
    private final ThreadPoolTaskExecutor inboxWorkerPool;
    private final InboxWorkerProperties properties;

    @Scheduled(fixedDelayString = "${tuning.inbox-worker.scheduling-interval}")
    public void processInbox() {
        List<InboxEntity> entities = inboxHandler
                .findNotProcessedInboxes(
                        properties.getLimitSize(),
                        properties.getRetryMax()
                );

        for (InboxEntity entity : entities) {
            inboxWorkerPool.execute(() -> {
                try {
                    inboxHandler.handle(entity);

                    inboxHandler.markProcessed(entity);
                } catch (Exception e) {
                    inboxHandler.markRetryOrFail(
                            entity,
                            properties.getRetryMax()
                    );
                }
            });
        }
    }
}
