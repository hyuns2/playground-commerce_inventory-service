package io.playground.inventoryservice.application.port;

import io.playground.inventoryservice.domain.Reservation;

import java.util.List;
import java.util.Map;

public interface ReservationPersistencePort {
    boolean existsByOrderExternalId(String orderExternalId);

    List<Reservation> findAllByOrderExternalIdAndStatus(String orderExternalId,
                                                        Reservation.ReservationStatus status);

    List<Reservation> findAllByOrderExternalIdAndVariantIdsAndStatuses(
            String orderExternalId,
            List<Long> variantId,
            List<Reservation.ReservationStatus> statuses
    );

    List<Integer> saveAll(List<Reservation> reservations);

    boolean updateStatusByIds(List<Long> ids,
                              Reservation.ReservationStatus status,
                              Reservation.ReservationStatus beforeStatus);

    boolean updateForAllRestoration(Map<Long, Integer> reservationQuantities);

    boolean updateForPartialRestoration(Map<Long, Integer> reservationQuantities,
                                        String idempotencyKey);
}
