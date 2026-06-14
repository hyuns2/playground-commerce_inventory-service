package io.playground.inventoryservice.application.inventory.port;

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

    boolean updateRestoredQuantityAndStatusByIds(boolean isPartially,
                                                 Map<Long, Integer> reservationQuantities);
}
