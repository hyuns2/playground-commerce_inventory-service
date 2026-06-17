package io.playground.inventoryservice.infrastructure.persistence.reservation;

import io.playground.inventoryservice.application.port.ReservationPersistencePort;
import io.playground.inventoryservice.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements ReservationPersistencePort {
    private final ReservationJpaRepository reservationRepository;
    private final ReservationJdbcTemplate reservationTemplate;

    @Override
    public boolean existsByOrderExternalId(String orderExternalId) {
        return reservationRepository.existsByOrderExternalId(orderExternalId);
    }

    @Override
    public List<Reservation> findAllByOrderExternalIdAndStatus(String orderExternalId,
                                                               Reservation.ReservationStatus status) {
        return reservationRepository
                .findAllByOrderExternalIdAndStatus(
                        orderExternalId, status
                ).stream()
                .map(ReservationEntity::toDomain)
                .toList();
    }

    @Override
    public List<Reservation> findAllByOrderExternalIdAndVariantIdsAndStatuses(
            String orderExternalId,
            List<Long> variantId,
            List<Reservation.ReservationStatus> statuses
    ) {
        return reservationRepository
                .findAllByOrderExternalIdAndVariantIdInAndStatusIn(
                        orderExternalId, variantId, statuses
                ).stream()
                .map(ReservationEntity::toDomain)
                .toList();
    }

    @Override
    public List<Integer> saveAll(List<Reservation> reservations) {
        return Arrays.stream(
                reservationTemplate.saveAll(
                        reservations
                )
        ).boxed().toList();
    }

    @Override
    public boolean updateStatusByIds(List<Long> ids,
                                     Reservation.ReservationStatus status,
                                     Reservation.ReservationStatus beforeStatus) {
        return Arrays.stream(
                reservationTemplate.updateStatusByIds(
                        ids, status, beforeStatus
                )
        ).allMatch(v -> v > 0);
    }

    @Override
    public boolean updateForAllRestoration(Map<Long, Integer> reservationQuantities) {
        return Arrays.stream(
                reservationTemplate.updateForAllRestoration(
                        reservationQuantities
                )
        ).allMatch(v -> v > 0);
    }

    @Override
    public boolean updateForPartialRestoration(Map<Long, Integer> reservationQuantities,
                                               String idempotencyKey) {
        return Arrays.stream(
                reservationTemplate.updateForPartialRestoration(
                        reservationQuantities, idempotencyKey
                )
        ).allMatch(v -> v > 0);
    }
}
