package io.playground.inventoryservice.infrastructure.persistence.reservation;

import io.playground.inventoryservice.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {
    boolean existsByOrderExternalId(String orderExternalId);

    List<ReservationEntity> findAllByOrderExternalIdAndStatus(String orderExternalId,
                                                              Reservation.ReservationStatus status);

    List<ReservationEntity> findAllByOrderExternalIdAndVariantIdInAndStatusIn(
            String orderExternalId,
            List<Long> variantId,
            List<Reservation.ReservationStatus> statuses
    );
}
