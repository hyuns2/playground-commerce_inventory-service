package io.playground.inventoryservice.infrastructure.persistence.eventstream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InboxJpaRepository extends JpaRepository<InboxEntity, Long> {
    @Query(value = """
            SELECT * FROM inboxes
            WHERE retry_count <= :retryMax
                AND (locked_until IS NULL OR locked_until < NOW())
                AND processed = false
            ORDER BY occurred_at ASC
            LIMIT :limitSize
            FOR UPDATE SKIP LOCKED;
    """, nativeQuery = true)
    List<InboxEntity> findNotProcessedInboxes(int limitSize,
                                              int retryMax);
}
