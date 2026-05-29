package io.playground.inventoryservice.infrastructure.persistence.eventstream;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxJpaRepository extends JpaRepository<InboxEntity, String> {
}
