package io.playground.inventoryservice.infrastructure.persistence.eventstream;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InboxPersistenceAdapter {
    private final InboxJpaRepository inboxRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<InboxEntity> findNotProcessedInboxes(int limitSize,
                                                     int retryMax) {
        return inboxRepository.findNotProcessedInboxes(
                limitSize, retryMax
        );
    }

    public boolean updateProcessedById(Long id, boolean processed) {
        return jdbcTemplate.update(
                "UPDATE inboxes SET " +
                        "processed = :processed " +
                        "WHERE id = :id",
                Map.of(
                        "id", id,
                        "processed", processed
                )
        ) == 1;
    }

    public boolean updateRetryCountAndLockedUntil(Long id, Instant lockedUntil) {
        return jdbcTemplate.update(
                "UPDATE inboxes " +
                        "SET retry_count = retry_count + 1, " +
                        "AND locked_until = :lockedUntil " +
                        "WHERE id = :id",
                Map.of(
                        "id", id,
                        "locked_until", lockedUntil
                )
        ) == 1;
    }
}
