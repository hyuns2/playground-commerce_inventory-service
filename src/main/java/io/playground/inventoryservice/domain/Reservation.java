package io.playground.inventoryservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reservation {
    private Long id;

    private Long stockId;

    private String orderExternalId;

    private Long variantId;

    private int quantity;

    private int restoredQuantity;

    private ReservationStatus status;

    public enum ReservationStatus {
        RESERVED, CONFIRMED, RELEASED,
        RESTORED, PARTIAL_RESTORED
    }

    public static Reservation of(Long id,
                                 Long stockId,
                                 String orderExternalId,
                                 Long variantId,
                                 int quantity,
                                 int restoredQuantity,
                                 ReservationStatus status) {
        return new Reservation(id, stockId, orderExternalId, variantId, quantity, restoredQuantity, status);
    }
}
